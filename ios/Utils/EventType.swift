import Foundation

enum EventType: String, CaseIterable {
    case ORIENTATION_DID_UPDATE = "orientationDidUpdate"
    case ON_INCREMENT = "onIncrement"
    static func allRawValues() -> [String] {
        return allCases.map { $0.rawValue }
    }
}
