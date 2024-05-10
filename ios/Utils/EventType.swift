import Foundation

enum EventType: String, CaseIterable {
    case ORIENTATION_DID_UPDATE = "orientationDidUpdate"
    static func allRawValues() -> [String] {
        return allCases.map { $0.rawValue }
    }
}
