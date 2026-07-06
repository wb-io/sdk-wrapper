# WBExchange SDK

SDK для интеграции обмена валют и криптоплатежей на ваш сайт.

## 🚀 Быстрый старт

### 1. Подключение скрипта

Добавьте скрипт SDK на вашу страницу:


### 2. Создание контейнера

Создайте HTML-элемент, в который будет встроен SDK:

```html
<div id="wbExchangeSdkWrapper" style="width: 360px; height: 640px;"></div>

wbExchangeSdk.setup({
  el: document.getElementById('wbExchangeSdkWrapper'),
  mode: wbExchangeSdk.mode.TokensMode,
  merchantId: 'YOUR_MERCHANT_ID',
  accessToken: 'USER_ACCESS_TOKEN',
  refreshToken: 'USER_REFRESH_TOKEN',
  
  // Обработчики событий
  onPayment: ({ orderId, transactionId }) => {
    console.log('Оплата прошла успешно!', orderId, transactionId);
  },
  
  onExit: () => {
    console.log('Пользователь закрыл SDK');
    wbExchangeSdk.cleanup();
  }
});

| Режим        | Описание                                   | Обязательные параметры                                      |
|--------------|--------------------------------------------|-------------------------------------------------------------|
| **AuthMode** | Полная авторизация пользователя через SDK  | `el`, `mode`, `merchantId`                                  |
| **LoginMode**| Только логин (без регистрации)             | `el`, `mode`, `merchantId`                                  |
| **TokensMode**| Работа с уже авторизованным пользователем | `el`, `mode`, `merchantId`, `accessToken`, `refreshToken`   |


| Параметр      | Тип          | Описание                                              |
|---------------|--------------|-------------------------------------------------------|
| `el`          | HTMLElement  | HTML-элемент контейнера для iframe                    |
| `mode`        | string       | Режим работы: `'AuthMode'` \| `'LoginMode'` \| `'TokensMode'` |
| `merchantId`  | string       | ID мерчанта в системе WBExchange                      |


| Параметр       | Тип      | Описание                      |
|----------------|----------|-------------------------------|
| `accessToken`  | string   | Токен доступа пользователя    |
| `refreshToken` | string   | Токен обновления              |

| Параметр              | Тип       | Описание                                      |
|-----------------------|-----------|-----------------------------------------------|
| `currencyAmount`      | number    | Сумма обмена (отдаваемая)                     |
| `currencyFrom`        | string    | Исходная валюта (например, `'USD'`, `'BTC'`)  |
| `currencyTo`          | string    | Целевая валюта                                |
| `currencyToAmount`    | number    | Сумма обмена (получаемая)                     |
| `disableCurrencyFrom` | boolean   | Заблокировать выбор исходной валюты в UI      |
| `disableCurrencyTo`   | boolean   | Заблокировать выбор целевой валюты в UI       |
| `disableAmount`       | boolean   | Заблокировать ввод суммы                      |

| Параметр                  | Тип       | Описание                                      |
|---------------------------|-----------|-----------------------------------------------|
| `email`                   | string    | Email пользователя                            |
| `merchantPass`            | string    | Пароль мерчанта                               |
| `externalClientId`        | string    | Внешний ID клиента в вашей системе            |
| `cryptoWallet`            | string    | Адрес крипто-кошелька                         |
| `hostUrl`                 | string    | URL хоста для возврата из внешних flow        |
| `providerType`            | string    | Тип провайдера                                |
| `redirectUrl`             | string    | URL перенаправления после завершения          |
| `startAppPage`            | string    | Стартовая страница (например, `'/account/payments'`) |
| `showBackButtonOnHomePage`| boolean   | Показывать кнопку "Назад" на главной          |
| `disableAddCard`          | boolean   | Скрыть возможность добавления карт            |
| `isAuthAgent`             | boolean   | Режим агента авторизации                      |
| `isBitcash`               | boolean   | Флаг интеграции с Bitcash App                 |
| `isTgBot`                 | boolean   | Флаг интеграции с Telegram Bot                |
| `debug`                   | boolean   | Включить режим отладки (подробные логи)       |

| Событие             | Параметры                                            | Описание                                              |
|---------------------|------------------------------------------------------|-------------------------------------------------------|
| `onLogin`           | `{ email, accessToken, refreshToken, isUserVerified }` | Вызывается после успешной авторизации               |
| `onUserData`        | `{ email, accessToken, refreshToken }`               | Вызывается при получении данных пользователя (LoginMode) |
| `onOrderCreated`    | `{ orderId, internalCryptoAddress }`                 | Вызывается при создании заявки на обмен               |
| `onOrderCompleted`  | `{ orderId, status }`                                | Вызывается при завершении заявки                      |
| `onPayment`         | `{ orderId, transactionId }`                         | Вызывается при успешной оплате                        |
| `onExit`            | —                                                    | Вызывается при нажатии кнопки выхода/закрытия SDK     |

onLogin: ({ email, accessToken, refreshToken, isUserVerified }) => {
  console.log('Пользователь авторизован:', email);
  // Сохраните токены в вашей системе
}

onUserData: ({ email, accessToken, refreshToken }) => {
  console.log('Данные пользователя:', email);
}

onOrderCreated: ({ orderId, internalCryptoAddress }) => {
  console.log('Заявка создана:', orderId);
}

onOrderCompleted: ({ orderId, status }) => {
  console.log('Заявка завершена:', orderId, status);
}

onPayment: ({ orderId, transactionId }) => {
  console.log('Оплата прошла:', transactionId);
}

onExit: () => {
  console.log('Пользователь вышел');
  wbExchangeSdk.cleanup();
}

wbExchangeSdk.setup({
  el: document.getElementById('sdk-wrapper'),
  mode: 'TokensMode',
  merchantId: 'MERCHANT_123',
  accessToken: 'USER_TOKEN',
  refreshToken: 'REFRESH_TOKEN',
  currencyFrom: 'USD',
  currencyTo: 'BTC',
  currencyAmount: 100,
  onPayment: (data) => alert('Оплачено!')
});

wbExchangeSdk.setup({
  el: document.getElementById('sdk-wrapper'),
  mode: 'AuthMode',
  merchantId: 'MERCHANT_123',
  onLogin: ({ email, accessToken, refreshToken }) => {
    // Сохраните токены и используйте для последующих запросов
    localStorage.setItem('accessToken', accessToken);
  }
});


