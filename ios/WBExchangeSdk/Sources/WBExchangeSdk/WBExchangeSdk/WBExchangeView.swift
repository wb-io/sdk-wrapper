import SwiftUI
import WebKit
import Foundation

enum PostMessageType: String {
    case OnChangeTokens = "OnChangeTokens"
    case OnBackButton = "OnBackButton"
}

struct PostMessageValue: Codable {
    let type: String // <- PostMessageType ??

    let accessToken: String?
    let isUserVerified: Bool?
}

public struct WBExchangeView: View {
    
    @ObservedObject var sdkConfig: WBExchangeSdkConfig

    public init(config: WBExchangeSdkConfig){
        sdkConfig = config
    }
    
    public var body: some View {
                
        VStack {
            if sdkConfig.showWebView {
                WhiteBirdWebView(url: sdkConfig.getUrl(), sdkConfig: sdkConfig)
            }
            else {
                Spacer()
            }
        }
    }

    public func goBack(){
        self.sdkConfig.sdklog("SDK: --------------------")
        self.sdkConfig.sdklog("SDK: goBack")
        if sdkConfig.webView == nil { return }
        
        let goBackResult = sdkConfig.webView?.goBack()
        if goBackResult == nil {
            sdkConfig.invokeOnExitHandler()
        }
    }
}

#Preview {
    var config = WBExchangeSdkConfig(
        mode: WBExchangeSdkMode.LoginMode,
        merchantId: "merchantId_TEST"
    )
        
    WBExchangeView(config: config)
}

struct WhiteBirdWebView: UIViewRepresentable {
    
    let url: String
    let sdkConfig: WBExchangeSdkConfig
    
    func makeUIView(context: Context) -> WKWebView {
        let coordinator = makeCoordinator()
        let userContentController = WKUserContentController()
        userContentController.add(coordinator, name: "WBSdkJsApi")
        // in javascript -> webkit.messageHandlers.WBSdkJsApi.postMessage("jsonString")
        
        let configuration = WKWebViewConfiguration()
        configuration.userContentController = userContentController
        
        let _wkwebview = WKWebView(frame: .zero, configuration: configuration)
        _wkwebview.navigationDelegate = coordinator
        
        if #available(iOS 16.4, *) {
            _wkwebview.isInspectable = true // devtools in safari
        } else {
            // Fallback on earlier versions
        }
        
        sdkConfig.webView = _wkwebview
        
        return _wkwebview
    }
    
    func updateUIView(_ webView: WKWebView, context: Context) {
        if let _ = webView.url {
            return
        }
        webView.load(URLRequest(url: URL(string: url)!))
    }
    
    func makeCoordinator() -> Coordinator {
        return Coordinator(config: sdkConfig)
    }

    class Coordinator: NSObject, WKNavigationDelegate, WKScriptMessageHandler {
        var webView: WKWebView?
        var sdkConfig: WBExchangeSdkConfig
        
        public init(config: WBExchangeSdkConfig){
            sdkConfig = config
        }
        
        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            self.webView = webView
        }
        
        // receive message from wkwebview
        func userContentController(
            _ userContentController: WKUserContentController,
            didReceive message: WKScriptMessage
        ) {
            self.sdkConfig.sdklog("------------------------------")
            self.sdkConfig.sdklog("message.name = \(message.name)")
            if message.name == "WBSdkJsApi" {
                sdkConfig.sdklog("message.body = \(message.body as! String)")
                
                jsonParse(message.body) { (postMessageValue: PostMessageValue) in
                    self.sdkConfig.sdklog("postMessageValue = \(postMessageValue)")
                    
                    if let type = postMessageValue.type as? String {
                        self.sdkConfig.sdklog("...type = \(type)")
                        
                        if type == PostMessageType.OnChangeTokens.rawValue {
                            let accessToken = postMessageValue.accessToken ?? ""
                            let isUserVerified = postMessageValue.isUserVerified ?? false
                            self.sdkConfig.sdklog("...accessToken = \(accessToken.suffix(20))")
                            self.sdkConfig.sdklog("...isUserVerified = \(isUserVerified)")
                            
                            self.sdkConfig.invokeOnLoginHandler(accessToken: accessToken, isUserVerified: isUserVerified)
                        }
                        
                        if type == PostMessageType.OnBackButton.rawValue {
                            self.sdkConfig.invokeOnExitHandler()
                        }
                    }
                }
            }
        }
        
        func jsonParse<T: Decodable>(_ json: Any, completion: @escaping ((T) -> Void)) {
            let jsonString = String(describing: json)
            let jsonData = Data(jsonString.utf8)
            let jsonDecoder = JSONDecoder()
        //    jsonDecoder.keyDecodingStrategy = .convertFromSnakeCase
            do {
                let model = try JSONDecoder().decode(T.self, from: jsonData)
                completion(model)
            } catch {
                sdkConfig.sdklog("!! jsonParse Error: \(error)")
                sdkConfig.sdklog("!! jsonParse Error: \(error.localizedDescription)")
            }
        }
    }
}
