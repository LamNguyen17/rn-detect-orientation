import Foundation

class EventEmitter {

    public static var shared = EventEmitter()

    private var eventEmitter: RnOrientation!

    func registerEventEmitter(eventEmitter: RnOrientation) {
        self.eventEmitter = eventEmitter
    }

    func dispatch(event: EventType, body: Any?) {
        self.eventEmitter.sendEvent(withName: event.rawValue, body: body)
    }
}
