import Combine
import WebKit
import Foundation
import OSLog

public enum WBExchangeSdkMode: String {
    case AuthMode = "AuthMode"
    case LoginMode = "LoginMode"
    case TokensMode = "TokensMode"
}

public class WBExchangeSdkConfig: ObservableObject {
            
    @Published private var mode: WBExchangeSdkMode
    @Published private var merchantId: String
    
    // TokensMode
    @Published private var accessToken: String
    @Published private var refreshToken: String
    
    // AuthMode
    private var onLoginHandler:((String, String, Bool) -> Void)?
    
    @Published private var showBackButtonOnHomePage: Bool
    private var onExitHandler:(() -> Void)?

    @Published private var disableAddCard: Bool
    
    public var webView: WKWebView?

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
        accessToken: String = "",
        refreshToken: String = "",
        showBackButtonOnHomePage: Bool = false,
        disableAddCard: Bool = false
    ) {
        self.mode = mode
        self.merchantId = merchantId
        self.accessToken = accessToken
        self.refreshToken = refreshToken
        self.showBackButtonOnHomePage = showBackButtonOnHomePage
        self.disableAddCard = disableAddCard
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
                
        // TODO: ?? move to ENV settings
        var url = "https://sdk.whitebird.io/v2.0/"
//        var url = "http://192.168.100.95:3004/"

        let modeQuery = "?mode=\(mode.rawValue)" // required
        
        let merchantIdQuery = "&merchantId=\(merchantId)" // required

        var showBackButton = false
        if showBackButtonOnHomePage && onExitHandler != nil
        {
            showBackButton = true
        }
        let showBackButtonQuery = "&showBackButtonOnHomePage=\(showBackButton ? "true" : "false")"

        let disableAddCardQuery = "&disableAddCard=\(disableAddCard ? "true" : "false")"
        
        var tokensQuery = ""
        if mode == WBExchangeSdkMode.TokensMode && !accessToken.isEmpty && !refreshToken.isEmpty {
            tokensQuery = "&access_token=\(accessToken)&refresh_token=\(refreshToken)"
        }

        return "\(url)\(modeQuery)\(merchantIdQuery)\(showBackButtonQuery)\(tokensQuery)\(disableAddCardQuery)"
    }
}
