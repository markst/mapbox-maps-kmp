import CoreLocation
import MapboxMaps
import UIKit

/// The `@objc` face this package puts on `MapboxMaps.MapView`.
///
/// `MapboxMaps` is pure Swift, and Kotlin/Native cinterop can only bind to Objective-C. Rather
/// than trying to get cinterop to find an ObjC face on Mapbox's Swift API (it can't — see the
/// root README for why that path is a dead end), this shim provides one directly. Only
/// Objective-C-representable types cross the boundary here: `String`, `Double`, `Bool`, and
/// escaping closures (which bridge to blocks). `CameraOptions`, `StyleURI`, and everything else
/// with a Swift-only shape stay behind this class.
///
/// A `UIViewController` (rather than a bare wrapper object) so it can be embedded directly via
/// Compose Multiplatform's `UIKitViewController` interop on the demo side.
@objc(MapboxMapController)
open class MapboxMapController: UIViewController {

    /// The underlying platform view, for platform-specific UI code that needs more than the
    /// `@objc` surface below exposes.
    public private(set) var mapView: MapView!

    private var styleLoadedCallback: (() -> Void)?

    // Kotlin/Native's ObjC interop requires a *designated* initializer to call as a super
    // constructor when subclassing — `UIViewController`'s inherited `init()` is only a
    // convenience initializer, which Kotlin/Native rejects (`Unable to call non-designated
    // initializer as super constructor`). Declaring this init (not marked `convenience`) makes
    // it a new designated initializer of this subclass, which in turn requires implementing
    // `init?(coder:)` per Swift's initializer inheritance rules.
    public init() {
        super.init(nibName: nil, bundle: nil)
    }

    @available(*, unavailable)
    public required init?(coder: NSCoder) {
        fatalError("init(coder:) is not supported")
    }

    open override func loadView() {
        let mapView = MapView(frame: .zero)
        self.mapView = mapView
        view = mapView
    }

    /// Loads a style by URI (e.g. `"mapbox://styles/mapbox/streets-v12"`). Silently ignores an
    /// unparseable URI — see `StyleURI.init?(rawValue:)` for what counts as valid.
    ///
    /// Named distinctly from Kotlin's `actual fun setStyleUri` (which calls this) — giving both
    /// sides of the boundary the same name would make the Kotlin subclass's `actual` member
    /// collide with this inherited one instead of wrapping it.
    @objc public func loadStyleUri(_ uri: String) {
        guard let styleURI = StyleURI(rawValue: uri) else {
            return
        }
        mapView.mapboxMap.loadStyle(styleURI) { [weak self] error in
            guard error == nil else { return }
            self?.styleLoadedCallback?()
        }
    }

    /// Moves the camera, optionally animating the transition. Takes primitives rather than a
    /// `CameraOptions` because Swift structs aren't representable across the ObjC boundary.
    /// Named distinctly from Kotlin's `actual fun setCamera` for the same reason as
    /// `loadStyleUri` above.
    @objc public func moveCamera(
        _ latitude: Double,
        longitude: Double,
        zoom: Double,
        bearing: Double,
        pitch: Double,
        animated: Bool
    ) {
        let options = CameraOptions(
            center: CLLocationCoordinate2D(latitude: latitude, longitude: longitude),
            zoom: CGFloat(zoom),
            bearing: bearing,
            pitch: CGFloat(pitch)
        )
        if animated {
            mapView.camera.fly(to: options)
        } else {
            mapView.mapboxMap.setCamera(to: options)
        }
    }

    /// Registers a callback invoked every time a style finishes loading. Kotlin/Native binds
    /// this as a trailing lambda, e.g. `subscribeStyleLoadedWithCallback { ... }`.
    @objc public func subscribeStyleLoaded(withCallback callback: @escaping () -> Void) {
        styleLoadedCallback = callback
    }
}
