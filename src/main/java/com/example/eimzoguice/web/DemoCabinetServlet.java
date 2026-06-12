package com.example.eimzoguice.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DemoCabinetServlet extends ServletSupport {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Object userInfo = session.getAttribute("USER_INFO");
        Object keyId = session.getAttribute("KEY_ID");

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html");
        response.getWriter().write(userInfo == null ? unauthorized() : authorized(userInfo.toString(), String.valueOf(keyId)));
    }

    private String unauthorized() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>E-IMZO Cabinet</title>
                </head>
                <body>
                <div>
                    <h3>You are not authorized</h3>
                    <a href="/demo/">Sign in</a>
                </div>
                </body>
                </html>
                """;
    }

    private String authorized(String userInfo, String keyId) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>E-IMZO Cabinet</title>
                    <script src="/demo/e-imzo.js" type="text/javascript"></script>
                    <script src="/demo/e-imzo-client.js" type="text/javascript"></script>
                    <script src="/demo/micro-ajax.js" type="text/javascript"></script>
                    <script src="/demo/e-imzo-init.js" type="text/javascript"></script>
                </head>
                <body>
                <div>
                    UserInfo: <div id="userInfo">%s</div>

                    <form name="testform">
                        <label id="message" style="color: red;"></label>
                        <br>
                        <p>Select signed document type:</p>
                        <input type="radio" id="attached" name="pkcs7Type" value="attached" onchange="pkcs7Type_changed()" checked="checked"><label for="attached">PKCS#7/Attached</label><br>
                        <input type="radio" id="detached" name="pkcs7Type" value="detached" onchange="pkcs7Type_changed()"><label for="detached">PKCS#7/Detached</label><br>
                        <br>
                        Text to sign <br>
                        <textarea name="data"></textarea><br>
                        <button onclick="sign()" type="button" id="signButton">Sign text</button><br>
                        <br>
                        File to sign <br>
                        <input type="file" id="fileInput" accept="*/*"><br>
                        <textarea name="fileData64"></textarea><br>
                        <button onclick="signFile()" type="button" id="signFileButton">Sign file</button><br>
                        <label id="progress"></label>
                        <br>
                        Key ID: <label id="keyId">%s</label><br>
                        <br>
                        <label id="pkcs7Type_label">Signed document PKCS#7</label><br>
                        <textarea name="pkcs7"></textarea><br>
                        <br>
                        <label>Verification result</label><br>
                        <textarea name="verifyResult"></textarea><br>
                    </form>
                </div>

                <script>
                    if (document.getElementById('fileInput')) {
                        document.getElementById('fileInput').addEventListener('change', function(event) {
                            var file = event.target.files[0];
                            if (file) {
                                var reader = new FileReader();
                                reader.onload = function(e) {
                                    document.testform.fileData64.value = e.target.result.split(',')[1];
                                };
                                reader.readAsDataURL(file);
                            }
                        });
                    }

                    var pkcs7Type_changed = function() {
                        var pkcs7Type = document.testform.pkcs7Type.value;
                        document.getElementById('pkcs7Type_label').innerHTML = pkcs7Type === "attached"
                            ? "Signed document PKCS#7/Attached (contains source document)"
                            : "Signed document PKCS#7/Detached (does not contain source document)";
                    };

                    var uiShowMessage = function(message) {
                        alert(message);
                    };

                    var uiLoading = function() {
                        var l = document.getElementById('message');
                        l.innerHTML = 'Loading ...';
                        l.style.color = 'red';
                    };

                    var uiNotLoaded = function(e) {
                        var l = document.getElementById('message');
                        l.innerHTML = '';
                        if (e) {
                            wsError(e);
                        } else {
                            uiShowMessage(errorBrowserWS);
                        }
                    };

                    var uiUpdateApp = function() {
                        document.getElementById('message').innerHTML = errorUpdateApp;
                    };

                    var uiAppLoad = function() {
                        if (document.testform) {
                            pkcs7Type_changed();
                            uiLoaded();
                        }
                    };

                    var uiLoaded = function() {
                        document.getElementById('message').innerHTML = '';
                    };

                    var uiShowProgress = function() {
                        var l = document.getElementById('progress');
                        l.innerHTML = 'Signing, please wait.';
                        l.style.color = 'green';
                    };

                    var uiHideProgress = function() {
                        document.getElementById('progress').innerHTML = '';
                    };

                    sign = function() {
                        uiShowProgress();
                        var pkcs7Type = document.testform.pkcs7Type.value;
                        var data = document.testform.data.value;
                        var keyId = document.getElementById('keyId').innerHTML;

                        EIMZOClient.createPkcs7(keyId, data, null, function(pkcs7) {
                            attachTimestamp(pkcs7, function(pkcs7wtst) {
                                document.testform.pkcs7.value = pkcs7wtst;
                                verify(pkcs7wtst, pkcs7Type === "detached", data, function(result) {
                                    document.testform.verifyResult.value = JSON.stringify(result, '', ' ');
                                });
                            });
                        }, uiHandleError, pkcs7Type === "detached");
                    };

                    signFile = function() {
                        uiShowProgress();
                        var pkcs7Type = document.testform.pkcs7Type.value;
                        var data64 = document.testform.fileData64.value;
                        var keyId = document.getElementById('keyId').innerHTML;

                        EIMZOClient.createPkcs7(keyId, data64, null, function(pkcs7) {
                            attachTimestamp(pkcs7, function(pkcs7wtst) {
                                document.testform.pkcs7.value = pkcs7wtst;
                                verify(pkcs7wtst, pkcs7Type === "detached", data64, function(result) {
                                    document.testform.verifyResult.value = JSON.stringify(result, '', ' ');
                                }, true);
                            });
                        }, uiHandleError, pkcs7Type === "detached", true);
                    };

                    attachTimestamp = function(pkcs7, callback) {
                        microAjax('/frontend/timestamp/pkcs7', function(data, s) {
                            uiHideProgress();
                            if (s.status !== 200) {
                                uiShowMessage(s.status + " - " + s.statusText);
                                return;
                            }
                            try {
                                data = JSON.parse(data);
                                if (data.status !== 1) {
                                    uiShowMessage(data.status + " - " + data.message);
                                    return;
                                }
                                callback(data.pkcs7b64);
                            } catch (e) {
                                uiShowMessage(s.status + " - " + s.statusText + ": " + e);
                            }
                        }, pkcs7);
                    };

                    verify = function(pkcs7wtst, detached, data, callback, isDataBase64Encoded) {
                        var data64;
                        if (detached) {
                            data64 = isDataBase64Encoded === true ? data : Base64.encode(data);
                        }
                        microAjax('verify', function(data, s) {
                            uiHideProgress();
                            if (s.status !== 200) {
                                uiShowMessage(s.status + " - " + s.statusText);
                                return;
                            }
                            try {
                                data = JSON.parse(data);
                                if (data.status !== 1) {
                                    uiShowMessage(data.status + " - " + data.message);
                                    return;
                                }
                                callback(data.pkcs7Info);
                            } catch (e) {
                                uiShowMessage(s.status + " - " + s.statusText + ": " + e);
                            }
                        }, 'pkcs7wtst=' + encodeURIComponent(pkcs7wtst) + (detached ? '&data64=' + encodeURIComponent(data64) : ""));
                    };

                    window.onload = AppLoad;
                </script>
                </body>
                </html>
                """.formatted(escapeHtml(userInfo), escapeHtml(keyId));
    }

    private String escapeHtml(String value) {
        return value == null ? "" : value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
