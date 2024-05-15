import Foundation
import UIKit

let PROJECT_NAME = "com.rnorientation.forest/RnOrientation"
let LANDSCAPE = "LANDSCAPE"
let PORTRAIT = "PORTRAIT"
let UNKNOWN = "UNKNOWN"

@objc(RnOrientation)
class RnOrientation: RCTEventEmitter {
    private var lastOrientation: String = "";
    private var isLocked: Bool = false
    private var count = 0
    private var window: UIWindow?
    private var orientationLock = UIInterfaceOrientationMask.portrait // Default to portrait

    public override init() {
        super.init()
        EventEmitter.shared.registerEventEmitter(eventEmitter: self)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(orientationDidUpdate),
                                               name: UIDevice.orientationDidChangeNotification, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self, name: UIDevice.orientationDidChangeNotification, object: nil)
    }
    
    private func sendEventJsLand(event: EventType, body: Any? = nil) {
        EventEmitter.shared.dispatch(event: event, body: body)
    }
    
    @objc func orientationDidUpdate(_ notification: Notification) {
        let orientation = getInterfaceOrientation()
        let orientationStr = getCurrentOrientation(orientation)
        lastOrientation = orientationStr
        sendEventJsLand(event: EventType.ORIENTATION_DID_UPDATE, body: ["orientation" : lastOrientation])
        print("Notification", orientationStr)
    }
    
    @objc
    func disableScreenOrientation() {
        DispatchQueue.main.async {
            self.lockToOrientation(.portrait, usingMask: .allButUpsideDown)
        }
    }
    
    @objc
    func enableScreenOrientation() {
        DispatchQueue.main.async {
            self.lockToOrientation(.unknown, usingMask: .all)
        }
    }
    
    //     @objc(multiply:withB:withResolver:withRejecter:)
    //     func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    //         resolve(a*b)
    //     }
    
    private func lockToOrientation(_ newOrientation: UIInterfaceOrientation, usingMask mask: UIInterfaceOrientationMask) {
        isLocked = true
        if #available(iOS 16.0, *) {
            guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene else {
                isLocked = false
                return
            }
            let geometryPreferences = UIWindowScene.GeometryPreferences.iOS(interfaceOrientations: mask)
            windowScene.requestGeometryUpdate(geometryPreferences) { error in
                print("Failed to update geometry with UIInterfaceOrientationMask: \(error)")
            }
        } else {
            UIDevice.current.setValue(newOrientation.rawValue, forKey: "orientation")
        }
        UIViewController.attemptRotationToDeviceOrientation()
        //      dispatch(event: EventType.ORIENTATION_DID_UPDATE, body: ["orientation": getCurrentOrientation(newOrientation)])
        isLocked = false
    }
    
    func getInterfaceOrientation() -> UIInterfaceOrientation {
        if #available(iOS 13.0, *) {
            var orientation: UIInterfaceOrientation = .unknown
            if let activeScene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
                orientation = activeScene.interfaceOrientation
            }
            return orientation
        } else {
            return UIApplication.shared.statusBarOrientation
        }
    }
    
    func getCurrentOrientation(_ orientation: UIInterfaceOrientation) -> String {
        switch orientation {
        case .portrait, .portraitUpsideDown:
            return PORTRAIT
        case .landscapeLeft, .landscapeRight:
            return LANDSCAPE
        default:
            return UNKNOWN
        }
    }
    
    // we need to override this method and
    // return an array of event names that we can listen to
    override func supportedEvents() -> [String]! {
        return EventType.allRawValues()
    }
    
    override public func constantsToExport() -> [AnyHashable : Any]! {
        let orientation = getInterfaceOrientation()
        let orientationStr = getCurrentOrientation(orientation)
        return ["initialOrientation": orientationStr]
    }
    
    override public static func requiresMainQueueSetup() -> Bool {
        return true
    }
}
