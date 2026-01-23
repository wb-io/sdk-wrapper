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

public typealias WBLoginHandler = (_ accessToken: String, _ refreshToken: String, _ isUserVerified: Bool) -> Void
public typealias WBUserDataHandler = (_ email: String, _ accessToken: String, _ refreshToken: String) -> Void
public typealias WBExitHandler = () -> Void
public typealias WBOrderCreatedHandler = (_ orderId: String, _ internalCryptoAddress: String?) -> Void

enum PostMessageType: String {
    case OnChangeTokens = "OnChangeTokens"
    case OnBackButton = "OnBackButton"
    case OnUserData = "OnUserData"
    case OnOrderCreated = "OnOrderCreated"
    case OnOpenLink = "OnOpenLink"
}

public class WBExchangeSdkConfig: ObservableObject {
    public var webView: WKWebView?
    
    // General config
    @Published private var mode: WBExchangeSdkMode
    @Published private var merchantId: String
    @Published private var merchantPass: String
    @Published private var externalClientId: String?
    @Published private var isAuthAgent: Bool
    
    // Exchange config
    @Published private var currencyAmount: Int?
    @Published private var currencyFrom: WBCurrency?
    @Published private var currencyTo: WBCurrency?
    @Published private var cryptoWallet: String?
    @Published private var disableCurrencyFrom: Bool
    @Published private var disableCurrencyTo: Bool
    
    // TokensMode
    @Published private var accessToken: String
    @Published private var refreshToken: String
    
    // AuthMode
    private var onLoginHandler: WBLoginHandler?
    
    // extra configurations:
    @Published private var redirectUrl: String?
    @Published private var email: String?
    @Published private var refId: String?
    @Published private var startAppPage: WBExchangeSdkStartPage = .home
    
    // Back button config, Any mode
    @Published private var showBackButtonOnHomePage: Bool
    private var onExitHandler: WBExitHandler?
    
    // LoginMode
    private var onUserDataHandler: WBUserDataHandler?
    // Any mode
    private var onOrderCreatedHandler: WBOrderCreatedHandler?
    
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
        isAuthAgent: Bool = false,
        
        currencyAmount: Int? = nil,
        currencyFrom: WBCurrency? = nil,
        currencyTo: WBCurrency? = nil,
        cryptoWallet: String? = nil,
        disableCurrencyFrom: Bool = false,
        disableCurrencyTo: Bool = false,
        
        accessToken: String = "",
        refreshToken: String = "",
        
        showBackButtonOnHomePage: Bool = false,
        redirectUrl: String? = nil,
        email: String? = nil,
        refId: String? = nil,
        startAppPage: WBExchangeSdkStartPage = .home,
        env: WBSdkEnv = .dev
    ) {
        self.mode = mode
        self.merchantId = merchantId
        self.merchantPass = merchantPass
        self.externalClientId = externalClientId
        self.isAuthAgent = isAuthAgent
        
        self.currencyAmount = currencyAmount
        self.currencyFrom = currencyFrom
        self.currencyTo = currencyTo
        self.cryptoWallet = cryptoWallet
        self.disableCurrencyFrom = disableCurrencyFrom
        self.disableCurrencyTo = disableCurrencyTo
        
        self.accessToken = accessToken
        self.refreshToken = refreshToken
        
        self.showBackButtonOnHomePage = showBackButtonOnHomePage
        self.redirectUrl = redirectUrl
        self.email = email
        self.refId = refId
        self.startAppPage = startAppPage
        self.env = env
    }
    
    public func initHandlers(
        onLogin: WBLoginHandler? = nil,
        onExit: WBExitHandler? = nil,
        onUserData: WBUserDataHandler? = nil,
        onOrderCreated: WBOrderCreatedHandler? = nil,
    ) {
        self.onLoginHandler = onLogin
        self.onExitHandler = onExit
        self.onUserDataHandler = onUserData
        self.onOrderCreatedHandler = onOrderCreated
    }

    // --------------------

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
            "isAuthAgent=\(isAuthAgent)",
            
            "currencyAmount=\(getNullableParam(currencyAmount))",
            "currencyFrom=\(getNullableParam(currencyFrom))",
            "currencyTo=\(getNullableParam(currencyTo))",
            "cryptoWallet=\(getNullableParam(cryptoWallet))",
            "disableCurrencyFrom=\(disableCurrencyFrom)",
            "disableCurrencyTo=\(disableCurrencyTo)",
            
            "showBackButtonOnHomePage=\(showBackButton)",
            "redirectUrl=\(getNullableParam(redirectUrl))",
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
    
    func invokeMessageHandler(_ message: PostMessageValue) {
        guard let type = message.type as? String else {
            sdklog("Unrecognized message type")
            return
        }
            
        sdklog("...type = \(type)")
        
        if type == PostMessageType.OnBackButton.rawValue {
            invokeOnExitHandler()
        }
        
        if type == PostMessageType.OnOrderCreated.rawValue {
            let orderId = message.orderId ?? ""
            sdklog("-> onOrderCreatedHandler... \(orderId)")
            onOrderCreatedHandler?(orderId, message.internalCryptoAddress)
        }
        
        if type == PostMessageType.OnChangeTokens.rawValue && mode == .AuthMode {
            let accessToken = message.accessToken ?? ""
            let refreshToken = message.refreshToken ?? ""
            let isUserVerified = message.isUserVerified ?? false
            sdklog("...accessToken = \(accessToken.suffix(20))")
            sdklog("...refreshToken = \(refreshToken.suffix(20))")
            sdklog("...isUserVerified = \(isUserVerified)")
            sdklog("-> invokeOnLoginHandler...")
            onLoginHandler?(accessToken, refreshToken, isUserVerified)
        }
        
        if type == PostMessageType.OnUserData.rawValue && mode == .LoginMode {
            let accessToken = message.accessToken ?? ""
            let refreshToken = message.refreshToken ?? ""
            let email = message.email ?? ""
            sdklog("-> onUserDataHandler... \(email)")
            onUserDataHandler?(email, accessToken, refreshToken)
        }
        
        if type == PostMessageType.OnOpenLink.rawValue {
            if let link = message.link {
                UIApplication.shared.open(URL(string: link)!, options: [:], completionHandler: { _ in
                    self.sdklog("-> OnOpenLink opened...")
                })
            }
            sdklog("-> OnOpenLink fired...")
        }
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
