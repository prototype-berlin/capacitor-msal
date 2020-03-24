import Foundation
import Capacitor
import MSAL

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(Msal)
public class Msal: CAPPlugin {
        var scopes: [String]?
        var accessToken = String()
        var applicationContext : MSALPublicClientApplication?
        var webViewParamaters : MSALWebviewParameters?
        var callPlugin : CAPPluginCall?
        
        @objc func getAccessToken(_ call: CAPPluginCall)  {
            self.callPlugin = call
            
            guard let clientId = call.options["clientId"] as? String else {
              call.reject("Must provide a clientId")
              return
            }
            
            guard let authorityUrl = call.options["authority"] as? String else {
              call.reject("Must provide an authority")
              return
            }
            
            scopes = (self.callPlugin?.get("scopes", [String].self)) ?? []
     
            guard let authorityURL = URL(string: authorityUrl) else {
                call.reject("Unable to create authority URL")
                return
            }
                        
            do {
                let authority = try MSALB2CAuthority(url: authorityURL)
                let msalConfiguration = MSALPublicClientApplicationConfig(clientId: clientId, redirectUri: nil, authority: authority)
                msalConfiguration.knownAuthorities = [authority]
                self.applicationContext = try MSALPublicClientApplication(configuration: msalConfiguration)
                self.webViewParamaters = MSALWebviewParameters(parentViewController: self.bridge.viewController)
            } catch {
                call.reject("\(error)")
                return
            }
                            
            guard let currentAccount = self.currentAccount(call) else {
                // We check to see if we have a current logged in account.
                // If we don't, then we need to sign someone in.
                acquireTokenInteractively()
                return
            }
                        
            acquireTokenSilently(currentAccount)
        }
        
        func currentAccount(_ call: CAPPluginCall) -> MSALAccount? {
            
            guard let applicationContext = self.applicationContext else { return nil }
            
            // We retrieve our current account by getting the first account from cache
            // In multi-account applications, account should be retrieved by home account identifier or username instead
            
            do {
                
                let cachedAccounts = try applicationContext.allAccounts()
                
                if !cachedAccounts.isEmpty {
                    return cachedAccounts.first
                }
            } catch let error as NSError {
                call.reject("Didn't find any accounts in cache: \(error)")
            }
            return nil
        }
        
        func acquireTokenSilently(_ account : MSALAccount!) {
            
            guard let applicationContext = self.applicationContext else { return }
            
            /**
             
             Acquire a token for an existing account silently
             
             - forScopes:           Permissions you want included in the access token received
             in the result in the completionBlock. Not all scopes are
             guaranteed to be included in the access token returned.
             - account:             An account object that we retrieved from the application object before that the
             authentication flow will be locked down to.
             - completionBlock:     The completion block that will be called when the authentication
             flow completes, or encounters an error.
             */
            
            let parameters = MSALSilentTokenParameters(scopes: scopes!, account: account)
            applicationContext.acquireTokenSilent(with: parameters) { (result, error) in
                if let error = error {
                    
                    let nsError = error as NSError
                    
                    // interactionRequired means we need to ask the user to sign-in. This usually happens
                    // when the user's Refresh Token is expired or if the user has changed their password
                    // among other possible reasons.
                    
                    if (nsError.domain == MSALErrorDomain) {
                        
                        if (nsError.code == MSALError.interactionRequired.rawValue) {
                            
                            DispatchQueue.main.async {
                                self.acquireTokenInteractively()
                            }
                            return
                        }
                    }
                    print(error)
                    
                    self.callPlugin?.reject("error acquiring token silently", error)
                    return
                }
                guard let result = result else {
                    
                    return
                }
                
                self.accessToken = result.accessToken
                self.callPlugin?.resolve([
                    "accessToken": self.accessToken
                ])
            }
        }
        
        func acquireTokenInteractively() {
            guard let applicationContext = self.applicationContext else { return }
            guard let webViewParameters = self.webViewParamaters else { return }

            let parameters = MSALInteractiveTokenParameters(scopes: scopes!, webviewParameters: webViewParameters)
            parameters.promptType = .selectAccount;
             
            applicationContext.acquireToken(with: parameters) { (result, error) in
                if let error = error {
                    let nsError = error as NSError
                    
                    if (nsError.domain == MSALErrorDomain) {
                        
                        if (nsError.code == MSALError.userCanceled.rawValue) {
                            
                            self.callPlugin?.reject("user_canceled", nil, ["userCanceled":true])
                            return
                        }
                    }
                    print(error)
                    
                    self.callPlugin?.reject("error acquiring token interactively", error)
                    return
                }
                
                guard let result = result else {

                    return
                }
                 
                self.accessToken = result.accessToken
                self.callPlugin?.resolve([
                    "accessToken": self.accessToken
                ])
             }
         }
}
