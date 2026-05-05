/**
 * @fileoverview Документация WBExchange SDK
 * @namespace wbExchangeSdk
 */

/**
 * Режимы работы SDK
 * @typedef {Object} SdkMode
 * @property {string} AuthMode - Режим авторизации
 * @property {string} LoginMode - Режим логина
 * @property {string} TokensMode - Режим с токенами
 */

/**
 * Параметры колбэка onLogin
 * @typedef {Object} LoginCallbackParams
 * @property {string} email - Email пользователя
 * @property {string} accessToken - Токен доступа
 * @property {string} refreshToken - Токен обновления
 * @property {boolean} isUserVerified - Статус верификации
 */

/**
 * Конфигурация SDK
 * @typedef {Object} SetupConfig
 * @property {HTMLElement} el - HTML-элемент контейнера
 * @property {string} mode - Режим работы
 * @property {string} merchantId - ID мерчанта
 * @property {string} [accessToken] - Токен доступа
 * @property {string} [refreshToken] - Токен обновления
 * @property {string} [email] - Email
 * @property {string} [merchantPass] - Пароль мерчанта
 * @property {string} [externalClientId] - Внешний ID клиента
 * @property {number} [currencyAmount] - Сумма обмена
 * @property {string} [currencyFrom] - Исходная валюта
 * @property {string} [currencyTo] - Целевая валюта
 * @property {boolean} [disableCurrencyFrom] - Отключить валюту from
 * @property {boolean} [disableCurrencyTo] - Отключить валюту to
 * @property {boolean} [isAuthAgent] - Отключить кнопку logout
 * @property {string} [cryptoWallet] - Крипто-кошелек
 * @property {string} [hostUrl] - URL хоста для возврата из внешних flow
 * @property {string} [redirectUrl] - URL перенаправления
 * @property {string} [startAppPage] - Стартовая страница
 * @property {boolean} [showBackButtonOnHomePage] - Кнопка назад
 * @property {boolean} [disableAddCard] - Отключить карты
 * @property {boolean} [debug] - Режим отладки
 * @property {function(LoginCallbackParams):void} [onLogin] - Колбэк логина
 * @property {function} [onUserData] - Колбэк данных пользователя
 * @property {function} [onOrderCreated] - Колбэк создания заказа
 * @property {function} [onPayment] - Колбэк ({transactionId, orderId}) => void
 * @property {function} [onExit] - Колбэк выхода
 */

/**
 * Инициализация SDK
 * @function setup
 * @memberof wbExchangeSdk
 * @param {SetupConfig} config - Конфигурация
 */

/**
 * Очистка SDK
 * @function cleanup
 * @memberof wbExchangeSdk
 */
