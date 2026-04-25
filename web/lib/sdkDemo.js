const clone = (obj) =>
  globalThis.structuredClone
    ? structuredClone(obj)
    : JSON.parse(JSON.stringify(obj));

const STORAGE_KEY = "cnf_config_v1";

const DEFAULT_CONFIG = {
  mode: "AuthMode",
  merchantId: "",

  accessToken: "",
  refreshToken: "",

  email: "",
  merchantPass: "",
  externalClientId: "",

  currencyAmount: "",
  currencyFrom: "",
  currencyTo: "",
  currencyToAmount: "",

  cryptoWallet: "",
  redirectUrl: "",
  hostUrl: "",
  startAppPage: "",

  showBackButton: false,
  disableAddCard: false,
  disableCurrencyFrom: false,
  disableCurrencyTo: false,
  disableAmount: false,
  isAuthAgent: false,
};

const tail10 = (s) => {
  if (!s) return "";
  return `${s.length > 10 ? "..." : ""}${s.slice(-10)}`;
};

async function copyTextToClipboard(textToCopy) {
  try {
    await navigator.clipboard.writeText(textToCopy ?? "");
    return true;
  } catch (error) {
    console.error("failed to copy to clipboard. error=" + error);
    return false;
  }
}

const FIELD_DEFS = [
  {
    prop: "merchantId",
    inputId: "merchantId",
    valueId: "merchantIdValue",
    kind: "text",
    trim: true,
  },
  {
    prop: "accessToken",
    inputId: "accessToken",
    valueId: "accessTokenValue",
    kind: "text",
    trim: true,
    valueText: (v) => tail10(v),
    valueClass: (v) => (v?.length ? "hasValue" : ""),
    diff: false,
  },
  {
    prop: "refreshToken",
    inputId: "refreshToken",
    valueId: "refreshTokenValue",
    kind: "text",
    trim: true,
    valueText: (v) => tail10(v),
    valueClass: (v) => (v?.length ? "hasValue" : ""),
    diff: false,
  },

  {
    prop: "email",
    inputId: "email",
    valueId: "emailValue",
    kind: "text",
    trim: true,
  },
  {
    prop: "merchantPass",
    inputId: "merchantPass",
    valueId: "merchantPassValue",
    kind: "text",
    trim: true,
  },
  {
    prop: "externalClientId",
    inputId: "externalClientId",
    valueId: "externalClientIdValue",
    kind: "text",
    trim: true,
  },

  {
    prop: "currencyAmount",
    inputId: "currencyAmount",
    valueId: "currencyAmountValue",
    kind: "text",
    trim: true,
  },
  {
    prop: "currencyFrom",
    inputId: "currencyFrom",
    valueId: "currencyFromValue",
    kind: "select",
  },
  {
    prop: "currencyTo",
    inputId: "currencyTo",
    valueId: "currencyToValue",
    kind: "select",
  },
  {
    prop: "currencyToAmount",
    inputId: "currencyToAmount",
    valueId: "currencyToAmountValue",
    kind: "text",
    trim: true,
  },

  {
    prop: "cryptoWallet",
    inputId: "cryptoWallet",
    valueId: "cryptoWalletValue",
    kind: "text",
    trim: true,
  },
  {
    prop: "redirectUrl",
    inputId: "redirectUrl",
    valueId: "redirectUrlValue",
    kind: "text",
    trim: true,
  },
  {
    prop: "hostUrl",
    inputId: "hostUrl",
    valueId: "hostUrlValue",
    kind: "text",
    trim: true,
  },
  {
    prop: "startAppPage",
    inputId: "startAppPage",
    valueId: "startAppPageValue",
    kind: "select",
    valueText: (v, cfg) =>
      (cfg.mode === "TokensMode" || cfg.mode === "LoginMode") && v ? v : "",
  },

  {
    prop: "showBackButton",
    inputId: "showBackButton",
    valueId: "showBackButtonValue",
    kind: "checkbox",
    toggleOnValueClick: true,
  },
  {
    prop: "disableAddCard",
    inputId: "disableAddCard",
    valueId: "disableAddCardValue",
    kind: "checkbox",
    toggleOnValueClick: true,
  },
  {
    prop: "disableCurrencyFrom",
    inputId: "disableCurrencyFrom",
    valueId: "disableCurrencyFromValue",
    kind: "checkbox",
  },
  {
    prop: "disableCurrencyTo",
    inputId: "disableCurrencyTo",
    valueId: "disableCurrencyToValue",
    kind: "checkbox",
  },
  {
    prop: "disableAmount",
    inputId: "disableAmount",
    valueId: "disableAmountValue",
    kind: "checkbox",
  },
  {
    prop: "isAuthAgent",
    inputId: "isAuthAgent",
    valueId: "isAuthAgentValue",
    kind: "checkbox",
  },
];

// ---------- main ----------
class SdkDemo {
  #config = clone(DEFAULT_CONFIG);
  #appliedConfig = clone(DEFAULT_CONFIG);

  #initSdkFn;
  #cleanupSdkFn;

  #dom = {
    fields: {},
    setupCodeWrapper: null,
    copyTo: null,
    copied: null,

    sdkMode: null,
    sdkModeRadioBtns: [],

    userDataWrapper: null,

    updateSdkBtn: null,
    sdkAgainBtn: null,
  };

  constructor({ initSdkFn, cleanupSdkFn }) {
    this.#initSdkFn = initSdkFn;
    this.#cleanupSdkFn = cleanupSdkFn;

    this.#config = this.#loadConfig();
    this.#appliedConfig = clone(this.#config);

    this.#initDom();
    this.#initValues();
    this.#initHandlers();

    this.#updateUI();

    queueMicrotask(() => {
      this.#initSdkFn?.();
    });
  }

  get config() {
    return clone(this.#config);
  }

  #loadConfig() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) return clone(DEFAULT_CONFIG);

      const parsed = JSON.parse(raw);
      return { ...clone(DEFAULT_CONFIG), ...parsed };
    } catch {
      return clone(DEFAULT_CONFIG);
    }
  }

  #saveConfig() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(this.#config));
  }

  #isFormControl(el) {
    return (
      el instanceof HTMLInputElement ||
      el instanceof HTMLTextAreaElement ||
      el instanceof HTMLSelectElement
    );
  }

  #readInput(def, el) {
    if (!el) return this.#config[def.prop];

    if (this.#isFormControl(el)) {
      if (def.kind === "checkbox") return Boolean(el.checked);
      const v = el.value ?? "";
      return def.trim ? v.trim() : v;
    }

    const v = el.textContent ?? "";
    return def.trim ? v.trim() : v;
  }

  #writeInput(def, el, value) {
    if (!el) return;

    if (this.#isFormControl(el)) {
      if (def.kind === "checkbox") el.checked = Boolean(value);
      else el.value = value ?? "";
      return;
    }

    el.textContent = value ?? "";
  }

  #valueText(def, value) {
    if (def.kind === "checkbox") return value ? "true" : "false";
    return value ?? "";
  }

  #initDom() {
    this.#dom.setupCodeWrapper = document.getElementById("setupCode");
    this.#dom.copyTo = document.getElementById("copyTo");
    this.#dom.copied = document.getElementById("copied");

    this.#dom.sdkMode = document.getElementById("sdkMode");
    this.#dom.sdkModeRadioBtns = Array.from(
      document.querySelectorAll('input[name="sdkMode"]'),
    );

    this.#dom.userDataWrapper = document.getElementById("userData");

    this.#dom.updateSdkBtn = document.getElementById("updateSdk");
    this.#dom.sdkAgainBtn = document.getElementById("sdk-again");

    for (const def of FIELD_DEFS) {
      this.#dom.fields[def.prop] = {
        input: document.getElementById(def.inputId),
        value: document.getElementById(def.valueId),
      };
    }
  }

  #initValues() {
    this.#dom.sdkModeRadioBtns.forEach((rb) => {
      rb.checked = rb.value === this.#config.mode;
    });

    for (const def of FIELD_DEFS) {
      const { input } = this.#dom.fields[def.prop] || {};
      this.#writeInput(def, input, this.#config[def.prop]);
    }
  }

  #initHandlers() {
    this.#dom.sdkModeRadioBtns.forEach((rb) => {
      rb.addEventListener("change", (e) => {
        this.#setConfig("mode", e.target.value);
      });
    });

    for (const def of FIELD_DEFS) {
      const { input, value } = this.#dom.fields[def.prop] || {};
      if (!input) continue;

      const evt =
        def.kind === "checkbox" || def.kind === "select" ? "change" : "input";

      input.addEventListener(evt, () => {
        const nextValue = this.#readInput(def, input);
        this.#setConfig(def.prop, nextValue);
      });

      if (def.toggleOnValueClick && value) {
        value.addEventListener("click", () => {
          this.#setConfig(def.prop, !this.#config[def.prop]);
        });
      }
    }

    this.#dom.sdkAgainBtn?.addEventListener("click", () => {
      this.#dom.sdkAgainBtn.style.display = "none";
      console.log(
        "%c MAIN_APP init SDK again =",
        "background:#ff0;color:#000;",
      );
      this.#initSdkFn?.();
    });

    this.#dom.updateSdkBtn?.addEventListener("click", (e) => {
      e.preventDefault();

      this.#cleanupSdkFn?.();
      this.#showAgainBtn(false);

      this.#appliedConfig = clone(this.#config);
      this.#updateUI();

      this.#initSdkFn?.();
    });

    this.#dom.copyTo?.addEventListener("click", async () => {
      const ok = await copyTextToClipboard(
        this.#dom.setupCodeWrapper?.textContent,
      );
      if (!ok) return;

      if (this.#dom.copied) this.#dom.copied.className = "";
      setTimeout(() => {
        if (this.#dom.copied) this.#dom.copied.className = "go";
      }, 20);
    });
  }

  // ---------------- core ----------------
  #setConfig(propName, value) {
    this.#config[propName] = value;
    this.#saveConfig();
    this.#updateUI(propName);
  }

  #setDiffClass(el, isDirty, pulse) {
    if (!el) return;

    if (!isDirty) {
      el.className = "";
      return;
    }

    if (pulse) {
      el.className = el.className === "prev" ? "changed" : "prev";
      return;
    }

    if (!el.className) el.className = "changed";
  }

  #updateUI(pulseProp = null) {
    const isAuthMode = this.#config.mode === "AuthMode";
    const isLoginMode = this.#config.mode === "LoginMode";
    const isTokensMode = this.#config.mode === "TokensMode";

    if (this.#dom.sdkMode) {
      this.#dom.sdkMode.textContent = this.#config.mode;
      const dirty = this.#config.mode !== this.#appliedConfig.mode;
      this.#setDiffClass(this.#dom.sdkMode, dirty, pulseProp === "mode");
    }

    if (this.#dom.userDataWrapper) {
      this.#dom.userDataWrapper.style.display =
        isLoginMode || isAuthMode ? "flex" : "none";
    }

    for (const def of FIELD_DEFS) {
      const nodes = this.#dom.fields[def.prop];
      if (!nodes) continue;

      const { input, value } = nodes;
      const current = this.#config[def.prop];
      const applied = this.#appliedConfig[def.prop];

      if (def.kind === "checkbox" && input) input.checked = Boolean(current);

      if (value) {
        value.textContent = def.valueText
          ? def.valueText(current, this.#config)
          : this.#valueText(def, current);

        if (typeof def.valueClass === "function") {
          value.className = def.valueClass(current) || "";
        } else if (def.diff !== false) {
          const dirty = current !== applied;
          this.#setDiffClass(value, dirty, pulseProp === def.prop);
        }
      }
    }

    const isDisableFrom = this.#config.disableCurrencyFrom;
    const isDisableTo = this.#config.disableCurrencyTo;
    const isDisableAmount = this.#config.disableAmount;

    const fromInput = this.#dom.fields.currencyFrom?.input;
    const toInput = this.#dom.fields.currencyTo?.input;
    const amountInput = this.#dom.fields.currencyAmount?.input;
    const toAmountInput = this.#dom.fields.currencyToAmount?.input;
    const walletInput = this.#dom.fields.cryptoWallet?.input;

    if (fromInput) fromInput.disabled = isDisableFrom;
    if (toInput) toInput.disabled = isDisableTo;
    if (amountInput) amountInput.disabled = isDisableFrom || isDisableAmount;
    if (toAmountInput) toAmountInput.disabled = isDisableAmount;
    if (walletInput) walletInput.disabled = isDisableTo;
  }

  showAgainBtn() {
    this.#showAgainBtn(true);
  }

  #showAgainBtn(isShow) {
    if (this.#dom.sdkAgainBtn) {
      this.#dom.sdkAgainBtn.style.display = isShow ? "flex" : "none";
    }
  }
}
