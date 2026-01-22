import SwiftUI
import WBExchangeSdk

struct ContentView: View {

    @State var isShowWB = false
    @State var wbExchangeView: WBExchangeView? = nil
    
    @StateObject var wbExchangeSdkConfig = WBExchangeSdkConfig(

        mode: WBExchangeSdkMode.LoginMode,
        merchantId: "11111111-1111-1111-1111-111111111111",
        merchantPass: "==",
        
        currencyAmount: 1000,
        currencyFrom: .RUB,

        // TokensMode
        accessToken: "...",
        refreshToken: "...",
        showBackButtonOnHomePage: true,
        email: "test@gmail.com",
        startAppPage: .home,
        env: .dev,
    )
        
    var body: some View {
        VStack {
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
                    .ignoresSafeArea()
//                WBExchangeView(config: wbExchangeSdkConfig)
            } else {
                Spacer()
            }
        }
//        .padding()
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
            print(wbExchangeSdkConfig.getUrl())
        }
//        .ignoresSafeArea()
    }
}

#Preview {
    ContentView()
}
