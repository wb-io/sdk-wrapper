(function () {
  const SdkMode = {
    AuthMode: "AuthMode",
    LoginMode: "LoginMode",
    TokensMode: "TokensMode",
  };

  const PostMessageType = {
    OnChangeTokens: "OnChangeTokens",
    OnBackButton: "OnBackButton",
    OnUserData: "OnUserData",
    OnOrderCreated: "OnOrderCreated",
    OnOpenLink: "OnOpenLink",
    OnOrderCompleted: "OnOrderCompleted",
  };

  const openFromTelegramTop = (url) => {
    const tg = window.Telegram?.WebApp;

    try {
      if (tg) {
        tg.ready();
        tg.showConfirm("Перейти к оплате", (ok) => {
          if (!ok) return;
          tg.openLink(url, { try_browser: "external" });
        });
      } else {
        window.open(url, "_blank", "noopener,noreferrer");
      }
    } catch (e) {
      window.location.href = url;
    }
  };

  // ----------------------------------------------------
  const version = "/v2.0";

  const SDK_ORIGIN = "https://sdk.dev.wbdevel.net";
  const SDK_URL = SDK_ORIGIN + version;

  //Only local test
  // const SDK_ORIGIN = "http://localhost:3000";
  // const SDK_URL = SDK_ORIGIN;

  const defaultConfig = {
    sdkIframe: undefined,

    el: undefined,
    mode: undefined,
    merchantId: "",

    // AuthMode
    onLoginHandler: undefined,

    // LoginMode
    onUserDataHandler: undefined,

    // TokensMode
    accessToken: "",
    refreshToken: "",

    // optional params
    email: "",
    merchantPass: "", // header authorization basic token
    externalClientId: "",
    currencyAmount: "",
    currencyFrom: "",
    currencyTo: "",
    currencyToAmount: "",
    disableCurrencyFrom: false,
    disableCurrencyTo: false,
    disableAmount: false,
    isAuthAgent: false,
    cryptoWallet: "",
    redirectUrl: "",
    startAppPage: "",
    refId: "",
    showBackButtonOnHomePage: false,
    disableAddCard: false,
    onOrderCreatedHandler: undefined,
    onExitHandler: undefined,
    isTgBot: false,
  };

  let config = Object.assign({}, defaultConfig);

  // ----------------------------------------------------

  const setup = (params) => {
    if (params.debug) {
      const logColor = "background:#ff0;color:#000;";
      console.info(`%c wbExchangeSdkConfig`, logColor, params);
    }

    config.isTgBot = params.isTgBot ?? Boolean(window.Telegram?.WebApp);

    if (params.el && config.el !== params.el && config.sdkIframe) {
      config.sdkIframe?.parentElement?.removeChild(config.sdkIframe);
      config.sdkIframe = undefined;
    }

    if (params.el !== undefined) config.el = params.el;
    if (params.mode !== undefined) {
      config.mode =
        params.mode === SdkMode.AuthMode ||
        params.mode === SdkMode.LoginMode ||
        params.mode === SdkMode.TokensMode
          ? params.mode
          : undefined;
    }
    if (params.merchantId !== undefined) config.merchantId = params.merchantId;

    if (params.onLogin !== undefined) config.onLoginHandler = params.onLogin;
    if (params.onUserData !== undefined)
      config.onUserDataHandler = params.onUserData;

    if (params.accessToken !== undefined)
      config.accessToken = params.accessToken;
    if (params.refreshToken !== undefined)
      config.refreshToken = params.refreshToken;

    if (params.email !== undefined) config.email = params.email;
    if (params.merchantPass !== undefined)
      config.merchantPass = params.merchantPass;
    if (params.externalClientId !== undefined)
      config.externalClientId = params.externalClientId;
    if (params.currencyAmount !== undefined)
      config.currencyAmount = params.currencyAmount;
    if (params.currencyFrom !== undefined)
      config.currencyFrom = params.currencyFrom;
    if (params.currencyTo !== undefined) config.currencyTo = params.currencyTo;
    if (params.currencyToAmount !== undefined)
      config.currencyToAmount = params.currencyToAmount;
    if (params.disableCurrencyFrom !== undefined)
      config.disableCurrencyFrom = params.disableCurrencyFrom;
    if (params.disableCurrencyTo !== undefined)
      config.disableCurrencyTo = params.disableCurrencyTo;
    if (params.disableAmount !== undefined)
      config.disableAmount = params.disableAmount;
    if (params.isAuthAgent !== undefined)
      config.isAuthAgent = params.isAuthAgent;
    if (params.cryptoWallet !== undefined)
      config.cryptoWallet = params.cryptoWallet;
    if (params.redirectUrl !== undefined)
      config.redirectUrl = params.redirectUrl;
    if (params.startAppPage !== undefined)
      config.startAppPage = params.startAppPage;
    if (params.refId !== undefined) config.refId = params.refId;
    if (params.showBackButtonOnHomePage !== undefined) {
      config.showBackButtonOnHomePage = !!params.showBackButtonOnHomePage;
    }
    if (params.disableAddCard !== undefined) {
      config.disableAddCard = params.disableAddCard;
    }
    if (params.onOrderCreated !== undefined) {
      config.onOrderCreatedHandler = params.onOrderCreated;
    }
    if (params.onExit !== undefined) {
      config.onExitHandler = params.onExit;
    }
    if (params.onOrderCompleted !== undefined) {
      config.onOrderCompletedHandler = params.onOrderCompleted;
    }

    makeIframe();
  };

  // ----------------------------------------------------

  const makeIframe = () => {
    if (config.sdkIframe) return;

    if (!config.el || !config.mode || !config.merchantId) {
      !config.el &&
        console.error("wbExchangeSdk: ERROR -> no element to insert SDK");
      !config.mode &&
        console.error("wbExchangeSdk: ERROR -> SDK mode NOT selected");
      !config.merchantId &&
        console.error("wbExchangeSdk: ERROR -> merchantId NOT selected");
      return;
    }

    if (
      config.mode === SdkMode.TokensMode &&
      (!config.accessToken || !config.refreshToken)
    ) {
      console.error("wbExchangeSdk: ERROR -> TokensMode has no tokens");
      return;
    }

    config.sdkIframe = document.createElement("iframe");
    config.sdkIframe.frameBorder = "0";
    config.sdkIframe.style.width = "100%";
    config.sdkIframe.style.height = "100%";
    config.sdkIframe.style.display = "block";
    config.sdkIframe.allow = "camera; clipboard-read; clipboard-write";
    config.sdkIframe.src = getUrl();
    config.el.appendChild(config.sdkIframe);

    window.addEventListener("message", onPostMessageHandler);
  };

  // ----------------------------------------------------

  const getUrl = () => {
    const params = {
      mode: config.mode,
      merchantId: config.merchantId,
      access_token: config.accessToken,
      refresh_token: config.refreshToken,
      email: config.email,
      merchantPass: config.merchantPass,
      externalClientId: config.externalClientId,
      currencyAmount: config.currencyAmount,
      currencyFrom: config.currencyFrom,
      currencyTo: config.currencyTo,
      currencyToAmount: config.currencyToAmount,
      disableCurrencyFrom: config.disableCurrencyFrom,
      disableCurrencyTo: config.disableCurrencyTo,
      disableAmount: config.disableAmount,
      isAuthAgent: config.isAuthAgent,
      cryptoWallet: config.cryptoWallet,
      redirectUrl: config.redirectUrl,
      startAppPage: config.startAppPage,
      refId: config.refId,
      showBackButtonOnHomePage: config.showBackButtonOnHomePage,
      disableAddCard: config.disableAddCard,
      isTgBot: config.isTgBot,
    };

    const queryString = Object.entries(params)
      .filter(([_, value]) => value)
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join("&");

    return `${SDK_URL}/?${queryString}`;
  };

  // ----------------------------------------------------

  const onPostMessageHandler = (event) => {
    if (event.origin !== SDK_ORIGIN) return;
    let data = {};
    try {
      data =
        typeof event.data === "string" ? JSON.parse(event.data) : event.data;
    } catch (e) {
      console.error(
        "wbExchangeSdk: ERROR -> postMessage data error",
        event.data,
      );
      return;
    }
    if (data?.type === PostMessageType.OnOpenLink) {
      openFromTelegramTop(data.link);
    }

    if (
      data?.type === PostMessageType.OnChangeTokens &&
      config.mode === SdkMode.AuthMode
    ) {
      config.onLoginHandler?.({
        email: data?.email,
        accessToken: data?.accessToken,
        refreshToken: data?.refreshToken,
        isUserVerified: data?.isUserVerified,
      });
    }
    if (
      data?.type === PostMessageType.OnUserData &&
      config.mode === SdkMode.LoginMode
    ) {
      config.onUserDataHandler?.({
        email: data?.email,
        accessToken: data?.accessToken,
        refreshToken: data?.refreshToken,
      });
    }
    if (data?.type === PostMessageType.OnOrderCreated) {
      config.onOrderCreatedHandler?.({
        orderId: data?.orderId,
        internalCryptoAddress: data?.internalCryptoAddress,
      });
    }
    if (data?.type === PostMessageType.OnOrderCompleted) {
      config.onOrderCompletedHandler?.({
        orderId: data?.orderId,
        status: data?.status,
      });
    }
    if (data?.type === PostMessageType.OnBackButton) {
      config.onExitHandler?.();
    }
  };

  // ----------------------------------------------------

  const cleanup = () => {
    config.sdkIframe?.parentElement?.removeChild(config.sdkIframe);
    config = Object.assign({}, defaultConfig);
    window.removeEventListener("message", onPostMessageHandler);
  };

  // ----------------------------------------------------

  window.wbExchangeSdk = {
    mode: SdkMode,
    setup,
    cleanup,
  };
})();
