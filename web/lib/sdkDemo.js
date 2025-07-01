class SdkDemo
{
  #config = {};

  get config()
  {
    return { ...this.#config };
  }

  // ----------------------------------------------------

  #initSdkFn;
  #cleanupSdkFn;

  #dom = {};

  constructor({ initSdkFn, cleanupSdkFn })
  {
    this.#initSdkFn = initSdkFn;
    this.#cleanupSdkFn = cleanupSdkFn;

    let config = {
      mode: localStorage.getItem('cnf_mode') || 'AuthMode',
      merchantId: localStorage.getItem('cnf_merchantId'),

      accessToken: localStorage.getItem('cnf_accessToken'),
      refreshToken: localStorage.getItem('cnf_refreshToken'),

      email: localStorage.getItem('cnf_email'),
      merchantPass: localStorage.getItem('cnf_merchantPass'),
      externalUserId: localStorage.getItem('cnf_externalUserId'),
      currencyAmount: localStorage.getItem('cnf_currencyAmount'),
      currencyFrom: localStorage.getItem('cnf_currencyFrom'),
      currencyTo: localStorage.getItem('cnf_currencyTo'),
      cryptoWallet: localStorage.getItem('cnf_cryptoWallet'),
      redirectUrl: localStorage.getItem('cnf_redirectUrl'),
      showBackButton: localStorage.getItem('cnf_showBackButton') === 'true',
      disableAddCard: localStorage.getItem('cnf_disableAddCard') === 'true',
    };

    this.#config = {
      ...config,

      modeInit: config.mode,
      modeUpdate: false,

      merchantIdInit: config.merchantId,
      merchantIdUpdate: false,

      emailInit: config.email,
      emailUpdate: false,

      merchantPassInit: config.merchantPass,
      merchantPassUpdate: false,

      externalUserIdInit: config.externalUserId,
      externalUserIdUpdate: false,

      currencyAmountInit: config.currencyAmount,
      currencyAmountUpdate: false,

      currencyFromInit: config.currencyFrom,
      currencyFromUpdate: false,

      currencyToInit: config.currencyTo,
      currencyToUpdate: false,

      cryptoWalletInit: config.cryptoWallet,
      cryptoWalletUpdate: false,

      redirectUrlInit: config.redirectUrl,
      redirectUrlUpdate: false,

      showBackButtonInit: config.showBackButton,
      showBackButtonUpdate: false,

      disableAddCardInit: config.disableAddCard,
      disableAddCardUpdate: false,
    };

    this.#initDom();
    this.#initValues();
    this.#initHandlers();

    this.#updateUI();

    setTimeout(() =>
    {
      this.#initSdkFn?.();
    });
  }

  // ----------------------------------------------------

  #initDom()
  {
    this.#dom.setupCodeWrapper = document.getElementById('setupCode');
    this.#dom.copyTo = document.getElementById('copyTo');
    this.#dom.copied = document.getElementById('copied');

    this.#dom.sdkMode = document.getElementById('sdkMode');
    this.#dom.sdkModeRadioBtns = document.querySelectorAll('input[name="sdkMode"]');

    this.#dom.merchantIdInput = document.getElementById('merchantId');
    this.#dom.merchantIdValue = document.getElementById('merchantIdValue');

    this.#dom.tokensWrapper = document.getElementById('tokens');
    this.#dom.userDataWrapper = document.getElementById('userData');

    this.#dom.accessTokenInput = document.getElementById('accessToken');
    this.#dom.accessTokenValue = document.getElementById('accessTokenValue');
    this.#dom.refreshTokenInput = document.getElementById('refreshToken');
    this.#dom.refreshTokenValue = document.getElementById('refreshTokenValue');

    this.#dom.emailInput = document.getElementById('email');
    this.#dom.emailValue = document.getElementById('emailValue');

    this.#dom.merchantPassInput = document.getElementById('merchantPass');
    this.#dom.merchantPassValue = document.getElementById('merchantPassValue');

    this.#dom.externalUserIdInput = document.getElementById('externalUserId');
    this.#dom.externalUserIdValue = document.getElementById('externalUserIdValue');

    this.#dom.currencyAmountInput = document.getElementById('currencyAmount');
    this.#dom.currencyAmountValue = document.getElementById('currencyAmountValue');

    this.#dom.currencyFromInput = document.getElementById('currencyFrom');
    this.#dom.currencyFromValue = document.getElementById('currencyFromValue');

    this.#dom.currencyToInput = document.getElementById('currencyTo');
    this.#dom.currencyToValue = document.getElementById('currencyToValue');

    this.#dom.cryptoWalletInput = document.getElementById('cryptoWallet');
    this.#dom.cryptoWalletValue = document.getElementById('cryptoWalletValue');

    this.#dom.redirectUrlInput = document.getElementById('redirectUrl');
    this.#dom.redirectUrlValue = document.getElementById('redirectUrlValue');

    this.#dom.showBackButtonInput = document.getElementById('showBackButton');
    this.#dom.showBackButtonValue = document.getElementById('showBackButtonValue');

    this.#dom.disableAddCardInput = document.getElementById('disableAddCard');
    this.#dom.disableAddCardValue = document.getElementById('disableAddCardValue');

    this.#dom.updateSdkBtn = document.getElementById('updateSdk');
    this.#dom.sdkAgainBtn = document.getElementById('sdk-again');
  }

  // ----------------------------------------------------

  #initValues()
  {
    this.#dom.sdkModeRadioBtns.forEach(radioBtn => radioBtn.checked = radioBtn.value === this.#config.mode);
    this.#dom.merchantIdInput.innerHTML = this.#config.merchantId;

    this.#dom.accessTokenInput.innerHTML = this.#config.accessToken;
    this.#dom.refreshTokenInput.innerHTML = this.#config.refreshToken;

    this.#dom.emailInput.value = this.#config.email;
    this.#dom.merchantPassInput.innerHTML = this.#config.merchantPass;
    this.#dom.externalUserIdInput.innerHTML = this.#config.externalUserId;
    this.#dom.currencyAmountInput.value = this.#config.currencyAmount;
    this.#dom.currencyFromInput.value = this.#config.currencyFrom;
    this.#dom.currencyToInput.value = this.#config.currencyTo;
    this.#dom.cryptoWalletInput.innerHTML = this.#config.cryptoWallet;
    this.#dom.redirectUrlInput.innerHTML = this.#config.redirectUrl;
    this.#dom.showBackButtonInput.checked = this.#config.showBackButton;
    this.#dom.disableAddCardInput.checked = this.#config.disableAddCard;
  }

  // ----------------------------------------------------

  #initHandlers()
  {
    this.#dom.sdkModeRadioBtns.forEach(radioBtn => radioBtn.addEventListener('change', e => this.#setConfig('mode', e.target.value)));

    this.#dom.merchantIdInput?.addEventListener('input', e => this.#setConfig('merchantId', e.target.value.trim()));

    const onChangeToken = token => e => this.#setConfig(token, e.target.value.trim());
    this.#dom.accessTokenInput?.addEventListener('input', onChangeToken('accessToken'));
    this.#dom.refreshTokenInput?.addEventListener('input', onChangeToken('refreshToken'));

    this.#dom.emailInput?.addEventListener('input', e => this.#setConfig('email', e.target.value.trim()));

    this.#dom.merchantPassInput?.addEventListener('input', e => this.#setConfig('merchantPass', e.target.value.trim()));

    this.#dom.externalUserIdInput?.addEventListener('input', e => this.#setConfig('externalUserId', e.target.value.trim()));

    this.#dom.currencyAmountInput?.addEventListener('input', e => this.#setConfig('currencyAmount', e.target.value.trim()));

    this.#dom.currencyFromInput?.addEventListener('change', e => this.#setConfig('currencyFrom', e.target.value));

    this.#dom.currencyToInput?.addEventListener('change', e => this.#setConfig('currencyTo', e.target.value));

    this.#dom.cryptoWalletInput?.addEventListener('input', e => this.#setConfig('cryptoWallet', e.target.value.trim()));

    this.#dom.redirectUrlInput?.addEventListener('input', e => this.#setConfig('redirectUrl', e.target.value.trim()));

    this.#dom.showBackButtonInput?.addEventListener('change', e => this.#setConfig('showBackButton', e.target.checked));
    this.#dom.showBackButtonValue?.addEventListener('click', () => this.#setConfig('showBackButton', !this.#config.showBackButton));

    this.#dom.disableAddCardInput?.addEventListener('change', e => this.#setConfig('disableAddCard', e.target.checked));
    this.#dom.disableAddCardValue?.addEventListener('click', () => this.#setConfig('disableAddCard', !this.#config.disableAddCard));

    // ----------------------------------------------------

    this.#dom.sdkAgainBtn?.addEventListener('click', () =>
    {
      this.#dom.sdkAgainBtn.style.display = 'none';
      console.log('%c MAIN_APP init SDK again =', 'background:#ff0;color:#000;');
      this.#initSdkFn?.();
    });

    // ----------------------------------------------------

    this.#dom.updateSdkBtn?.addEventListener('click', e =>
    {
      e.preventDefault();
      this.#config.modeInit = this.#config.mode;
      this.#config.merchantIdInit = this.#config.merchantId;
      this.#config.emailInit = this.#config.email;
      this.#config.merchantPassInit = this.#config.merchantPass;
      this.#config.externalUserIdInit = this.#config.externalUserId;
      this.#config.currencyAmountInput = this.#config.currencyAmount;
      this.#config.currencyFromInput = this.#config.currencyFrom;
      this.#config.currencyToInput = this.#config.currencyTo;
      this.#config.cryptoWalletInput = this.#config.cryptoWallet;
      this.#config.redirectUrlInit = this.#config.redirectUrl;
      this.#config.showBackButtonInit = this.#config.showBackButton;
      this.#config.disableAddCardInit = this.#config.disableAddCard;

      this.#cleanupSdkFn?.();
      this.#showAgainBtn(false);

      this.#updateUI();
      this.#initSdkFn?.();
    });

    // ----------------------------------------------------

    async function copyTextToClipboard(textToCopy)
    {
      try
      {
        await navigator.clipboard.writeText(textToCopy);
        return true;
      }
      catch( error )
      {
        console.error('failed to copy to clipboard. error=' + error);
      }
    }

    this.#dom.copyTo?.addEventListener('click', async () =>
    {
      if( !await copyTextToClipboard(this.#dom.setupCodeWrapper?.textContent) ) return;

      this.#dom.copied && (this.#dom.copied.className = '');
      setTimeout(() =>
      {
        this.#dom.copied && (this.#dom.copied.className = 'go');
      }, 20);
    });
  }

  // ----------------------------------------------------

  #setConfig(propName, value)
  {
    this.#config[propName] = value;
    this.#config[`${propName}Update`] = true;
    localStorage.setItem(`cnf_${propName}`, value);
    this.#updateUI();
  }

  // ----------------------------------------------------

  #updateUI()
  {
    const {
      sdkMode,
      tokensWrapper,
      userDataWrapper,
      accessTokenValue,
      refreshTokenValue,
      merchantIdValue,
      merchantPassValue,
      emailValue,
      externalUserIdValue,
      currencyAmountValue,
      currencyFromValue,
      currencyToValue,
      cryptoWalletValue,
      redirectUrlValue,
      disableAddCardValue,
      disableAddCardInput,
      showBackButtonValue,
      showBackButtonInput,
    } = this.#dom;

    sdkMode.textContent = this.#config.mode;
    if( this.#config.mode === this.#config.modeInit ) sdkMode.className = '';
    else this.#config.modeUpdate && (sdkMode.className = sdkMode.className === 'prev' ? 'changed' : 'prev');
    this.#config.modeUpdate = false;

    merchantIdValue.textContent = this.#config.merchantId;
    if( this.#config.merchantId === this.#config.merchantIdInit ) merchantIdValue.className = '';
    else this.#config.merchantIdUpdate && (merchantIdValue.className = merchantIdValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.merchantIdUpdate = false;

    const isAuthMode = this.#config.mode === 'AuthMode';
    const isLoginMode = this.#config.mode === 'LoginMode';
    const isTokensMode = this.#config.mode === 'TokensMode';
    tokensWrapper && (tokensWrapper.style.display = isTokensMode ? 'flex' : 'none');
    userDataWrapper && (userDataWrapper.style.display = isLoginMode || isAuthMode ? 'flex' : 'none');

    accessTokenValue.className = isTokensMode && this.#config.accessToken?.length ? 'hasValue' : '';
    accessTokenValue.textContent = isTokensMode && this.#config.accessToken ? `${this.#config.accessToken.length > 10 ? '...' : ''}${this.#config.accessToken.substr(-10, 10)}` : '';

    refreshTokenValue.className = isTokensMode && this.#config.refreshToken?.length ? 'hasValue' : '';
    refreshTokenValue.textContent = isTokensMode && this.#config.refreshToken ? `${this.#config.refreshToken.length > 10 ? '...' : ''}${this.#config.refreshToken.substr(-10, 10)}` : '';

    emailValue.textContent = this.#config.email;
    if( this.#config.email === this.#config.emailInit ) emailValue.className = '';
    else this.#config.emailUpdate && (emailValue.className = emailValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.emailUpdate = false;

    merchantPassValue.textContent = this.#config.merchantPass;
    if( this.#config.merchantPass === this.#config.merchantPassInit ) merchantPassValue.className = '';
    else this.#config.merchantPassUpdate && (merchantPassValue.className = merchantPassValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.merchantPassUpdate = false;

    externalUserIdValue.textContent = this.#config.externalUserId;
    if( this.#config.externalUserId === this.#config.externalUserIdInit ) externalUserIdValue.className = '';
    else this.#config.externalUserIdUpdate && (externalUserIdValue.className = externalUserIdValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.externalUserIdUpdate = false;

    currencyAmountValue.textContent = this.#config.currencyAmount;
    if( this.#config.currencyAmount === this.#config.currencyAmountInit ) currencyAmountValue.className = '';
    else this.#config.currencyAmountUpdate && (currencyAmountValue.className = currencyAmountValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.currencyAmountUpdate = false;

    currencyFromValue.textContent = this.#config.currencyFrom;
    if( this.#config.currencyFrom === this.#config.currencyFromInit ) currencyFromValue.className = '';
    else this.#config.currencyFromUpdate && (currencyFromValue.className = currencyFromValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.currencyFromUpdate = false;

    currencyToValue.textContent = this.#config.currencyTo;
    if( this.#config.currencyTo === this.#config.currencyToInit ) currencyToValue.className = '';
    else this.#config.currencyToUpdate && (currencyToValue.className = currencyToValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.currencyToUpdate = false;

    cryptoWalletValue.textContent = this.#config.cryptoWallet;
    if( this.#config.cryptoWallet === this.#config.cryptoWalletInit ) cryptoWalletValue.className = '';
    else this.#config.cryptoWalletUpdate && (cryptoWalletValue.className = cryptoWalletValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.cryptoWalletUpdate = false;

    redirectUrlValue.textContent = this.#config.redirectUrl;
    if( this.#config.redirectUrl === this.#config.redirectUrlInit ) refreshTokenValue.className = '';
    else this.#config.redirectUrlUpdate && (redirectUrlValue.className = redirectUrlValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.redirectUrlUpdate = false;

    showBackButtonInput.checked = this.#config.showBackButton;
    showBackButtonValue.textContent = this.#config.showBackButton ? 'true' : 'false';
    if( this.#config.showBackButton === this.#config.showBackButtonInit ) showBackButtonValue.className = '';
    else this.#config.showBackButtonUpdate && (showBackButtonValue.className = showBackButtonValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.showBackButtonUpdate = false;

    disableAddCardInput.checked = this.#config.disableAddCard;
    disableAddCardValue.textContent = this.#config.disableAddCard ? 'true' : 'false';
    if( this.#config.disableAddCard === this.#config.disableAddCardInit ) disableAddCardValue.className = '';
    else this.#config.disableAddCardUpdate && (disableAddCardValue.className = disableAddCardValue.className === 'prev' ? 'changed' : 'prev');
    this.#config.disableAddCardUpdate = false;
  }

  // ----------------------------------------------------

  showAgainBtn()
  {
    this.#showAgainBtn(true);
  }

  #showAgainBtn(isShow)
  {
    this.#dom.sdkAgainBtn && (this.#dom.sdkAgainBtn.style.display = isShow ? 'flex' : 'none');
  }

  // ----------------------------------------------------
}
