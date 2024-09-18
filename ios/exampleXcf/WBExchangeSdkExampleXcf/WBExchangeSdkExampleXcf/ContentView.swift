//
//  ContentView.swift
//  WBExchangeSdkExampleXcf
//
//  Created by max on 17.09.24.
//

import SwiftUI
import WBExchangeSdk

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")

            Text("WBExchangeSdk version =  \(WBExchangeSdk.version)")
                .padding()
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
