import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        Piley.init().doInit()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
