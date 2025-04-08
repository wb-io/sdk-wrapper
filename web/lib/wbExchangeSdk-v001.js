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
    OnUserData: 'OnUserData',
  };

  // ----------------------------------------------------

  const SDK_ORIGIN = 'https://sdk.qa.wbdevel.net';
  const SDK_URL = `${SDK_ORIGIN}/v2.0`;

  const defaultConfig = {
    sdkIframe: null,

    // required params
    el: null,       // sdk wrapper in some app -> html element
    mode: null,     // -> SdkMode
    merchantId: '', //

    // AuthMode
    onLoginHandler: null,

    // LoginMode
    onUserDataHandler: null,

    // TokensMode
    accessToken: '',
    refreshToken: '',

    // optional params
    email: '',
    merchantPass: '', // header authorization basic token
    externalUserId: '',
    currencyAmount: '',
    currencyFrom: '',
    currencyTo: '',
    cryptoWallet: '',
    showBackButtonOnHomePage: false,
    disableAddCard: false,
    onExitHandler: null,
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

    if (params.onLogin !== undefined) config.onLoginHandler = params.onLogin;
    if (params.onUserData !== undefined) config.onUserDataHandler = params.onUserData;

    if (params.accessToken !== undefined) config.accessToken = params.accessToken;
    if (params.refreshToken !== undefined) config.refreshToken = params.refreshToken;

    if (params.email !== undefined) config.email = params.email;
    if (params.merchantPass !== undefined) config.merchantPass = params.merchantPass;
    if (params.externalUserId !== undefined) config.externalUserId = params.externalUserId;
    if (params.currencyAmount !== undefined) config.currencyAmount = params.currencyAmount;
    if (params.currencyFrom !== undefined) config.currencyFrom = params.currencyFrom;
    if (params.currencyTo !== undefined) config.currencyTo = params.currencyTo;
    if (params.cryptoWallet !== undefined) config.cryptoWallet = params.cryptoWallet;
    if (params.showBackButtonOnHomePage !== undefined) // true | false
    {
      config.showBackButtonOnHomePage = !!params.showBackButtonOnHomePage;
    }
    if (params.disableAddCard !== undefined) config.disableAddCard = params.disableAddCard;
    if (params.onExit !== undefined) config.onExitHandler = params.onExit;

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

    if (config.cryptoWallet && !config.currencyTo)
    {
      console.error('wbExchangeSdk: ERROR -> should be passed currencyTo param');
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
    const params = {
      mode: config.mode,
      merchantId: config.merchantId,
      access_token: config.accessToken,
      refresh_token: config.refreshToken,
      email: config.email,
      merchantPass: config.merchantPass,
      externalUserId: config.externalUserId,
      currencyAmount: config.currencyAmount,
      currencyFrom: config.currencyFrom,
      currencyTo: config.currencyTo,
      cryptoWallet: config.cryptoWallet,
      showBackButtonOnHomePage: config.showBackButtonOnHomePage,
      disableAddCard: config.disableAddCard,
    };

    const queryString = Object.entries(params)
        .filter(([, value]) => value !== undefined && value !== null && value !== false)
        .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
        .join("&");

    return `${SDK_URL}/?${queryString}`;
  };

  // ----------------------------------------------------

  const onPostMessageHandler = (event) =>
  {
    if (event.origin !== SDK_ORIGIN) return;
    let data = {};
    try
    {
      data = typeof event.data === 'string' ? JSON.parse(event.data) : event.data;
    }
    catch( e )
    {
      console.error('wbExchangeSdk: ERROR -> postMessage data error', event.data);
      return;
    }
    if (data?.type === PostMessageType.OnChangeTokens && config.mode === SdkMode.AuthMode)
    {
      config.onLoginHandler?.({
        email: data?.email,
        accessToken: data?.accessToken,
        refreshToken: data?.refreshToken,
        isUserVerified: data?.isUserVerified,
      });
    }
    if (data?.type === PostMessageType.OnUserData && config.mode === SdkMode.LoginMode)
    {
      config.onUserDataHandler?.({
        email: data?.email,
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
