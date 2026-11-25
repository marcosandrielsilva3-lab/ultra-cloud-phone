# Roblox Floating Window — experimental

This Android app launches the installed Roblox app with Android ActivityOptions launch bounds, requesting a small freeform window where the device/launcher supports it.

Important limitations:
- It does not embed Roblox inside a custom app view.
- Android/Samsung must support and allow freeform or pop-up windows for Roblox.
- If the system ignores launch bounds, Roblox opens full screen. On supported Samsung builds, use Recents > Roblox app icon > Open in pop-up view (if available).
- Some Galaxy A03s / One UI Core builds may not expose pop-up view for third-party apps.

Build: JDK 17, Gradle 8.9, Android SDK 35; run gradle assembleDebug.
