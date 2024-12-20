import SwiftUI
import WBExchangeSdk

struct ContentView: View {

    @State var isShowWB = false
    @State var wbExchangeView:WBExchangeView? = nil
    
    @StateObject var wbExchangeSdkConfig = WBExchangeSdkConfig(

//        mode: WBExchangeSdkMode.AuthMode,
        mode: WBExchangeSdkMode.LoginMode,
//        mode: WBExchangeSdkMode.TokensMode,

//        merchantId: "merchantId_TEST",
        merchantId: "4f19017b-0793-4591-94ff-610bb3c4665b",

        // TokensMode
//        accessToken: "...",
//        refreshToken: "...",
                
        showBackButtonOnHomePage: true,
        
        disableAddCard: true
    )
        
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")

            Button("isShowWB = \(isShowWB)") {
                isShowWB.toggle()
                if isShowWB {
                    self.wbExchangeView = WBExchangeView(config: wbExchangeSdkConfig)
                } else {
                    self.wbExchangeView = nil
                }
            }

            if isShowWB {
                Button("send goBack to SDK") {
                    self.wbExchangeView?.goBack()
                }
            }

            if isShowWB {
                wbExchangeView
//                WBExchangeView(config: wbExchangeSdkConfig)
            } else {
                Spacer()
            }
        }
        .padding()
        .onAppear {
            wbExchangeSdkConfig.initHandlers(
                onLogin: {
                    accessToken, refreshToken, isUserVerified in
                    print("MAIN_APP: accessToken = \(accessToken.suffix(20))")
                    print("MAIN_APP: refreshToken = \(refreshToken.suffix(20))")
                    print("MAIN_APP: isUserVerified = \(isUserVerified)")
                },
                onExit: {
                    print("MAIN_APP: onExit")
                    isShowWB = false
                }
            )
        }
    }
}

//#Preview {
//    ContentView()
//}
