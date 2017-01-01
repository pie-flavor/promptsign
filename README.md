# promptsign

`promptsign` is a small Gradle plugin used for
avoiding configuration of the `signing` plugin.
If any of the required properties (key ID, keyring
file, secret key password) are not present, they're
requested. If in GUI mode, a popup is spawned; if in
CLI mode, they're requested via the console.
Note that this should replace essentially all `signing`
plugin related code, up to and including the application
of the plugin itself.
