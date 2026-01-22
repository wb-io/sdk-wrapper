export {};

declare global {
  interface Window {
    Telegram?: {
      WebApp: TelegramWebApp;
    };
  }

  interface TelegramWebApp {
    showConfirm(message: string, callback: (ok: boolean) => void): void;
    openLink(url: string, options?: { try_instant_view?: boolean }): void;
  }
}
