// @ts-ignore
declare module "@capacitor/core" {
  interface PluginRegistry {
    Msal: MsalPlugin;
  }
}

export interface MsalPlugin {
  getAccessToken(options: { clientId: string, authority: string, scopes: string[] }): Promise<{ accessToken: string }>;
}
