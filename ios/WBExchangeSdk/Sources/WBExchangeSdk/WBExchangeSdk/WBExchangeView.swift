import SwiftUI
import WebKit

public struct WBExchangeView: View {
    
    @ObservedObject var sdkConfig: WBExchangeSdkConfig

    public init(config: WBExchangeSdkConfig){
        sdkConfig = config
    }
    
    public var body: some View {
                
        VStack {
            if sdkConfig.showWebView {
                WhiteBirdWebView(url: sdkConfig.getUrl())
            }
            else if sdkConfig.showLoader {
                Spacer()
                ProgressView()
                Spacer()
            }
            else {
                Spacer()
            }
        }
    }
}

#Preview {
    var config = WBExchangeSdkConfig()
        
    config.setTokens(
        accessToken: "accessToken...",
        refreshToken: "refreshToken..."
    )

    return WBExchangeView(config: config)
}

struct DependencyInjector {
    static var webView: WKWebView?
    
    static func getWebView() -> WKWebView {
        if let webView = webView {
            return webView
        }
        webView = WKWebView()
        return webView!
    }
}

struct WhiteBirdWebView: UIViewRepresentable {
 
    let url: String

    let webView: WKWebView = DependencyInjector.getWebView()
    
    func makeUIView(context: Context) -> WKWebView {
        return webView
    }
    func updateUIView(_ uiView: WKWebView, context: Context) {
        if let _ = webView.url {
            return
        }
        webView.load(URLRequest(url: URL(string: url)!))
    }
}
