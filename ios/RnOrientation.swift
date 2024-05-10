import Foundation

let PROJECT_NAME = "com.rnorientation.forest/RnOrientation"
let LANDSCAPE = "LANDSCAPE"
let PORTRAIT = "PORTRAIT"
let UNKNOWN = "UNKNOWN"

@objc(RnOrientation)
class RnOrientation: RCTEventEmitter {
    private var lastOrientation: String = "";
    private var isLocked: Bool = false
    
    public override init() {
        super.init()
        EventEmitter.shared.registerEventEmitter(eventEmitter: self)
    }
    
    private func emit(event: EventType, body: Any? = nil) {
        EventEmitter.shared.dispatch(event: event, body: body)
    }
    
    func getInterfaceOrientation() -> UIInterfaceOrientation {
        if #available(iOS 13.0, *) {
            var orientation: UIInterfaceOrientation = .unknown
            if let activeScene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
                orientation = activeScene.interfaceOrientation
            }
            #if DEBUG
            if orientation == .unknown {
                print("Device orientation is unknown.")
            }
            #endif
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
    
    @objc(constantsToExport)
    override public func constantsToExport() -> [AnyHashable : Any]! {
        return ["initialOrientation": UNKNOWN]
    }
    
    override public static func requiresMainQueueSetup() -> Bool {
        return true
    }
    
    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
}
