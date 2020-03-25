// @ts-ignore
declare module "@capacitor/core" {
  interface PluginRegistry {
    Msal: MsalPlugin;
  }
}

export interface MsalPlugin {
  getAccessToken(options: { clientId: string, authority: string, scopes: string[] }): Promise<{ accessToken: string }>;
  getAccessTokenSilently(options: { clientId: string, authority: string, scopes: string[] }): Promise<{ accessToken: string }>;
  signOut(): Promise<void>;
}
