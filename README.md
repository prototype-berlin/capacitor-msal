# capacitor-msal-b2c

## Android
You need to place a config file in the plugin folder

`node_modules/capacitor-msal-b2c/android/src/main/res/raw/msal_config.json`

with the following layout:

```
{
    "client_id": "<your_client_id_here>",
    "redirect_uri": "<your_redirect_uri_here>",
    "authorities": [{
            "type": "B2C",
            "authority_url": "https://contoso.b2clogin.com/tfp/contoso.onmicrosoft.com/B2C_1_SISOPolicy/",
            "default": true
        },
        {
            "type": "B2C",
            "authority_url": "https://contoso.b2clogin.com/tfp/contoso.onmicrosoft.com/B2C_1_EditProfile/"
        }
    ]
}
```

More details in the [Microsoft Docs](https://docs.microsoft.com/de-de/azure/active-directory/develop/msal-android-b2c#configure-known-authorities-and-redirect-uri)

## iOS

- For caching to work you need to add a Keychain Group in Keychain Sharing in Signing & Capabilities in Xcode
