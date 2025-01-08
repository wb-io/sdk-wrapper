(function()
{
  const SdkMode = {
    AuthMode: 'AuthMode',
    LoginMode: 'LoginMode',
    TokensMode: 'TokensMode',
  };

  const PostMessageType = {
    OnChangeTokens: 'OnChangeTokens',
    OnBackButton: 'OnBackButton',
  };

  // ----------------------------------------------------

  const SDK_ORIGIN = 'https://sdk.dev.wbdevel.net';
  const SDK_URL = `${SDK_ORIGIN}/v2.0`;

  const defaultConfig = {
    sdkIframe: null,

    el: null,       // sdk wrapper in some app -> html element
    merchantId: '', //
    mode: null,     // -> SdkMode

    // TokensMode
    accessToken: '',
    refreshToken: '',

    // AuthMode
    onLoginHandler: null,

    showBackButtonOnHomePage: false,
    onExitHandler: null,

    disableAddCard: false,
  };

  let config = Object.assign({}, defaultConfig);

  // ----------------------------------------------------

  const setup = (params) =>
  {
    if (params.el && config.el !== params.el && config.sdkIframe)
    {
      config.sdkIframe?.parentElement?.removeChild(config.sdkIframe);
      config.sdkIframe = null;
    }

    if (params.el !== undefined) config.el = params.el;
    if (params.mode !== undefined)
    {
      config.mode = (
        params.mode === SdkMode.AuthMode ||
        params.mode === SdkMode.LoginMode ||
        params.mode === SdkMode.TokensMode) ? params.mode : null;
    }
    if (params.merchantId !== undefined) config.merchantId = params.merchantId;

    if (params.accessToken !== undefined) config.accessToken = params.accessToken;
    if (params.refreshToken !== undefined) config.refreshToken = params.refreshToken;

    if (params.onLogin !== undefined) config.onLoginHandler = params.onLogin;

    if (params.showBackButtonOnHomePage !== undefined) // true | false
    {
      config.showBackButtonOnHomePage = !!params.showBackButtonOnHomePage;
    }
    if (params.onExit !== undefined) config.onExitHandler = params.onExit;
    if (params.disableAddCard !== undefined) config.disableAddCard = params.disableAddCard;

    makeIframe();
  };

  // ----------------------------------------------------

  const makeIframe = () =>
  {
    if (config.sdkIframe) return;

    if (!config.el || !config.mode || !config.merchantId)
    {
      !config.el && console.error('wbExchangeSdk: ERROR -> no element to insert SDK');
      !config.mode && console.error('wbExchangeSdk: ERROR -> SDK mode NOT selected');
      !config.merchantId && console.error('wbExchangeSdk: ERROR -> merchantId NOT selected');
      return;
    }

    if (config.mode === SdkMode.TokensMode && (!config.accessToken || !config.refreshToken))
    {
      console.error('wbExchangeSdk: ERROR -> TokensMode has no tokens');
      return;
    }

    config.sdkIframe = document.createElement('iframe');
    config.sdkIframe.frameBorder = '0';
    config.sdkIframe.style.width = '100%';
    config.sdkIframe.style.height = '100%';
    config.sdkIframe.style.display = 'block';
    config.sdkIframe.src = getUrl();
    config.el.appendChild(config.sdkIframe);

    window.addEventListener('message', onPostMessageHandler);
  };

  // ----------------------------------------------------

  const getUrl = () =>
  {
    let url = `${SDK_URL}/`;
    url += `?mode=${config.mode}`;
    url += `&merchantId=${config.merchantId}`;
    url += `&showBackButtonOnHomePage=${!!config.onExitHandler && config.showBackButtonOnHomePage}`;
    url += `&disableAddCard=${config.disableAddCard}`;

    if (config.mode === SdkMode.TokensMode && config.accessToken && config.refreshToken)
    {
      url += `&access_token=${config.accessToken}&refresh_token=${config.refreshToken}`;
    }

    return url;
  };

  // ----------------------------------------------------

  const onPostMessageHandler = (event) =>
  {
    if (event.origin !== SDK_ORIGIN) return;
    let data = {};
    try
    {
      data = JSON.parse(event.data);
    }
    catch( e )
    {
      console.error('wbExchangeSdk: ERROR -> postMessage data error', event.data);
      return;
    }
    if (data?.type === PostMessageType.OnChangeTokens && config.mode === SdkMode.AuthMode)
    {
      config.onLoginHandler?.({
        accessToken: data?.accessToken,
        refreshToken: data?.refreshToken,
        isUserVerified: data?.isUserVerified,
      });
    }
    if (data?.type === PostMessageType.OnBackButton)
    {
      config.onExitHandler?.();
    }
  };

  // ----------------------------------------------------

  const cleanup = () =>
  {
    config.sdkIframe?.parentElement?.removeChild(config.sdkIframe);
    config = Object.assign({}, defaultConfig);
    window.removeEventListener('message', onPostMessageHandler);
  };

  // ----------------------------------------------------

  window.wbExchangeSdk = {
    mode: SdkMode,
    setup,
    cleanup,
  };

})();
