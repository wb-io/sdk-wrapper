import Combine
import WebKit
import Foundation
import OSLog

public enum WBExchangeSdkMode: String {
    case AuthMode = "AuthMode"
    case LoginMode = "LoginMode"
    case TokensMode = "TokensMode"
}

public enum WBExchangeSdkStartPage: String {
    case home = ""
    case paymentMethods = "/account/payments"
    case transactionHistory = "/user-operations"
}

public enum WBSdkEnv: String {
    case dev
    case qa
    case prod
}

public enum WBCurrency {
    case BYN
    case RUB
    case USD
    case EUR
    case BTC
    case ETH
    case USDT // ERC-20
    case TRX
    case USDT_TRC // TRC-20
    case TON
    case USDT_TON // TON
    case WBP // TRC-20
    case USDC
}

public class WBExchangeSdkConfig: ObservableObject {
    public var webView: WKWebView?
    
    // General config
    @Published private var mode: WBExchangeSdkMode
    @Published private var merchantId: String
    @Published private var merchantPass: String
    @Published private var externalClientId: String?
    
    // Exchange config
    @Published private var currencyAmount: Int?
    @Published private var currencyFrom: WBCurrency?
    @Published private var currencyTo: WBCurrency?
    @Published private var cryptoWallet: String?
    
    // TokensMode
    @Published private var accessToken: String
    @Published private var refreshToken: String
    
    // AuthMode
    private var onLoginHandler:((String, String, Bool) -> Void)?
    
    // extra configurations:
    @Published private var email: String?
    @Published private var refId: String?
    @Published private var startAppPage: WBExchangeSdkStartPage = .home
    
    // Back button config
    @Published private var showBackButtonOnHomePage: Bool
    private var onExitHandler:(() -> Void)?
    
    private var env: WBSdkEnv

    // --------------------
    
    public var showWebView: Bool {
        (mode == WBExchangeSdkMode.LoginMode) ||
        (mode == WBExchangeSdkMode.AuthMode && onLoginHandler != nil) ||
        (mode == WBExchangeSdkMode.TokensMode && accessToken != "" && refreshToken != "")
    }

    // --------------------

    struct Log {
        static let systemId = "io.whitebird.sdk.exchange"
        static let systemLogger = Logger(subsystem: systemId, category: "System")
    }

    public func sdklog(_ msg:String){
//        print("sdklog: \(msg)")
        Log.systemLogger.info("sdklog: \(msg)")
    }
    
    // --------------------

    public init(
        mode: WBExchangeSdkMode,
        merchantId: String,
        merchantPass: String,
        externalClientId: String? = nil,
        
        currencyAmount: Int? = nil,
        currencyFrom: WBCurrency? = nil,
        currencyTo: WBCurrency? = nil,
        cryptoWallet: String? = nil,
        
        accessToken: String = "",
        refreshToken: String = "",
        
        showBackButtonOnHomePage: Bool = false,
        email: String? = nil,
        refId: String? = nil,
        startAppPage: WBExchangeSdkStartPage = .home,
        env: WBSdkEnv = .dev
    ) {
        self.mode = mode
        self.merchantId = merchantId
        self.merchantPass = merchantPass
        self.externalClientId = externalClientId
        
        self.currencyAmount = currencyAmount
        self.currencyFrom = currencyFrom
        self.currencyTo = currencyTo
        self.cryptoWallet = cryptoWallet
        
        self.accessToken = accessToken
        self.refreshToken = refreshToken
        
        self.showBackButtonOnHomePage = showBackButtonOnHomePage
        self.email = email
        self.refId = refId
        self.startAppPage = startAppPage
        self.env = env
    }
    
    public func initHandlers(
        onLogin: ((String, String, Bool) -> Void)? = nil,
        onExit: (() -> Void)? = nil
    ) {
        self.onLoginHandler = onLogin
        self.onExitHandler = onExit
    }

    // --------------------

    public func invokeOnLoginHandler(accessToken:String, refreshToken:String, isUserVerified:Bool)
    {
        sdklog("-> invokeOnLoginHandler...")
        onLoginHandler?(accessToken, refreshToken, isUserVerified)
    }

    public func invokeOnExitHandler()
    {
        sdklog("-> invokeOnExitHandler...")
        onExitHandler?()
    }
    
    // --------------------

    public func getUrl() -> String {
        let url = getBaseUrl()
        let showBackButton = showBackButtonOnHomePage && onExitHandler != nil
        var params = [
            "mode=\(mode.rawValue)",
            "merchantId=\(merchantId)",
            "merchantPass=\(merchantPass)",
            "externalClientId=\(getNullableParam(externalClientId))",
            
            "currencyAmount=\(getNullableParam(currencyAmount))",
            "currencyFrom=\(getNullableParam(currencyFrom))",
            "currencyTo=\(getNullableParam(currencyTo))",
            "cryptoWallet=\(getNullableParam(cryptoWallet))",
            
            "showBackButtonOnHomePage=\(showBackButton)",
            "email=\(getNullableParam(email))",
            "refId=\(getNullableParam(refId))",
            "startAppPage=\(startAppPage.rawValue)"
        ]
        
        if mode == WBExchangeSdkMode.TokensMode && !accessToken.isEmpty && !refreshToken.isEmpty {
            params.append("access_token=\(accessToken)")
            params.append("refresh_token=\(refreshToken)")
        }

        return "\(url)?\(params.joined(separator: "&"))"
    }
    
    private func getNullableParam<T>(_ param: T?) -> String {
        if let value = param {
            return "\(value)"
        }
        
        return ""
    }
    
    private func getBaseUrl() -> String {
        if env == .prod {
            return "https://sdk.whitebird.io/v2.0/"
        }
        
        if env == .qa {
            return "https://sdk.qa.wbdevel.net/v2.0/"
        }
        
        return "https://sdk.dev.wbdevel.net/v2.0/"
    }
}
