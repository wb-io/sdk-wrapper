import Combine
import Foundation

public class WBExchangeSdkConfig: ObservableObject {
    
    /// loginRequired - в данный момент не используется в sdk
    /// но (в задумке) если выставить loginRequired
    /// - лоадер игнорируется
    /// - и должна сразу отобразиться webView с формой логина через сайт WB
    @Published private var loginRequired: Bool
    
    /// отображать ли лоадер если еще не передан токен
    @Published private var showLoaderIfNoToken: Bool
    
    @Published private var accessToken = ""
    @Published private var refreshToken = ""

    public var showWebView: Bool { hasTokens || loginRequired } // loginRequired <- now ignored
    public var showLoader: Bool { !showWebView && showLoaderIfNoToken }
    public var hasTokens: Bool { !accessToken.isEmpty && !refreshToken.isEmpty }
    
    public init(
        showLoaderIfNoToken: Bool = false,
        loginRequired: Bool = false
    ) {
        self.showLoaderIfNoToken = showLoaderIfNoToken
        self.loginRequired = loginRequired
    }
    
    public func setTokens(accessToken: String, refreshToken: String)
    {
        self.accessToken = accessToken
        self.refreshToken = refreshToken
    }
    
    // "${URL_EXCHANGE_SERVER}?access_token=${config.accessToken}&refresh_token=${config.refreshToken}"
    public func getUrl() -> String {
        
        // TODO: ?? move to ENV settings
        var url = "http://192.168.100.95:3004/"

        if !accessToken.isEmpty && !refreshToken.isEmpty {
            url += "?access_token=\(accessToken)&refresh_token=\(refreshToken)"
        }
        return url
    }
}
