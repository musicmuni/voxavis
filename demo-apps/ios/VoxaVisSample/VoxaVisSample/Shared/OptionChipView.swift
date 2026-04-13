import SwiftUI
import UIKit

struct OptionChipView: View {
    let label: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(.caption)
                .fontWeight(selected ? .semibold : .regular)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(selected ? Color.accentColor.opacity(0.2) : Color(UIColor.secondarySystemBackground))
                .foregroundColor(selected ? .accentColor : .primary)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(selected ? Color.accentColor : Color.gray.opacity(0.3), lineWidth: selected ? 2 : 1)
                )
                .cornerRadius(8)
        }
        .buttonStyle(.plain)
    }
}
