declare namespace wbExchangeSdk {
  enum Mode {
    AuthMode = "AuthMode",
    LoginMode = "LoginMode",
    TokensMode = "TokensMode",
  }

  interface SetupConfig {
    el: HTMLElement;
    mode: Mode | string;
    merchantId: string;
    accessToken?: string;
    refreshToken?: string;
    email?: string;
    merchantPass?: string;
    externalClientId?: string;
    currencyAmount?: number;
    currencyFrom?: string;
    disableCurrencyFrom?: boolean;
    currencyTo?: string;
    currencyToAmount?: number;
    disableCurrencyTo?: boolean;
    disableAmount?: boolean;
    cryptoWallet?: string;
    hostUrl?: string;
    providerType?: string;
    redirectUrl?: string;
    startAppPage?: string;
    showBackButtonOnHomePage?: boolean;
    disableAddCard?: boolean;
    isAuthAgent?: boolean;
    isBitcash?: boolean;
    isTgBot?: boolean;
    color?: string;
    themeMode: "light" | "dark";
    debug?: boolean;
    onLogin?: (params: {
      email: string;
      accessToken: string;
      refreshToken: string;
      isUserVerified: boolean;
    }) => void;
    onUserData?: (params: {
      email: string;
      accessToken: string;
      refreshToken: string;
    }) => void;
    onOrderCreated?: (params: {
      orderId: string;
      internalCryptoAddress: string;
    }) => void;
    onOrderCompleted?: (params: { orderId: string; status: string }) => void;
    onPayment?: (params: { orderId: string; transactionId: string }) => void;
    onExit?: () => void;
  }

  function setup(config: SetupConfig): void;
  function cleanup(): void;

  const mode: typeof Mode;
}
