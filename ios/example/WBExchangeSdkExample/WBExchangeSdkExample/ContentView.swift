import SwiftUI
import WBExchangeSdk

struct ContentView: View {

    @State var isShowWB = false

    @StateObject var wbExchangeSdkConfig = WBExchangeSdkConfig(
        showLoaderIfNoToken: true
    )
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")

            Button("Toggle WB = \(isShowWB)") {
                isShowWB.toggle()
            }
            .padding()

            VStack {
                Text("has tokens = \(wbExchangeSdkConfig.hasTokens)")
                
                if wbExchangeSdkConfig.hasTokens {
                    Button("DELETE tokens...") {
                        wbExchangeSdkConfig.setTokens(
                            accessToken: "",
                            refreshToken: ""
                        )
                    }
                } else {
                    Button("SET tokens...") {
                        wbExchangeSdkConfig.setTokens(
                            accessToken: "accessToken...",
                            refreshToken: "refreshToken..."
                        )
                    }
                }
            }

            if isShowWB {
                WBExchangeView(config: wbExchangeSdkConfig)
            } else {
                Spacer()
            }
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
