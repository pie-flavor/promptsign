package flavor.pie.promptsign

import groovy.swing.SwingBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.signing.Sign

class PromptSignPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.with {
            plugins.apply('signing')
            signing.sign(configurations.archives)
            tasks.build.dependsOn tasks.signArchives
            gradle.taskGraph.whenReady { graph ->
                if (graph.allTasks.any { it instanceof Sign}) {
                    boolean needsId = false
                    boolean needsFile = false
                    boolean needsPass = false
                    try { project.ext."signing.keyId" } catch (ignore) { needsId = true }
                    try { project.ext."signing.secretKeyRingFile" } catch (ignore) { needsFile = true }
                    try { project.ext."signing.password" } catch (ignore) { needsPass = true }
                    if (System.console() == null) {
                        new SwingBuilder().edt {
                            dialog(modal: true,
                                    title: "PGP information",
                                    alwaysOnTop: true,
                                    resizable: false,
                                    locationRelativeTo: null,
                                    pack: true,
                                    show: true
                            ) {
                                vbox {
                                    def keyid
                                    if (needsId) {
                                        hbox {
                                            label (text: "PGP Key ID")
                                            keyid = textField()
                                        }
                                    }
                                    def keyfile
                                    if (needsFile) {
                                        hbox {
                                            label (text: "PGP keyring file")
                                            keyfile = textField()
                                        }
                                    }
                                    def keypass
                                    if (needsPass) {
                                        hbox {
                                            label (text: "PGP passkey")
                                            keypass = passwordField()
                                        }
                                    }
                                    button(defaultButton: true, text: 'OK', actionPerformed: {
                                        if (needsId) {
                                            project.ext."signing.keyId" = keyid.text
                                        }
                                        if (needsFile) {
                                            project.ext."signing.secretKeyRingFile" = keyfile.text
                                        }
                                        if (needsPass) {
                                            project.ext."signing.password" = keypass.text
                                        }
                                        dispose()
                                    })
                                }
                            }
                        }
                    } else {
                        if (needsId) {
                            project.ext."signing.keyId" = System.console().readLine("PGP Key ID: ")
                        }
                        if (needsFile) {
                            project.ext."signing.secretKeyRingFile" = System.console().readLine("PGP keyring file: ")
                        }
                        if (needsPass) {
                            project.ext."signing.password" = new String(System.console().readPassword("PGP passkey: "))
                        }
                    }
                }
            }
        }
    }
}
