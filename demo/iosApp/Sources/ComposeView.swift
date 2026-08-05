import DemoShared
import SwiftUI
import UIKit

/// Bridges into `demo/shared`'s Kotlin/Compose UI. `MainViewControllerKt.MainViewController()`
/// is the Swift-visible name Kotlin/Native generates for the top-level `MainViewController()`
/// function in `MainViewController.kt` — everything past this point (including embedding the
/// Mapbox shim) is Kotlin, not Swift.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
