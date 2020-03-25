// @ts-ignore
declare module "@capacitor/core" {
  interface PluginRegistry {
    Msal: MsalPlugin;
  }
}

export interface Options {
  clientId: string;
  authority: string;
  scopes: string[];
};

export interface AuthResponse {
  accessToken?: string;
};

export interface MsalPlugin {
  getAccessToken(options: Options): Promise<AuthResponse>;
  getAccessTokenSilently(options: Options): Promise<AuthResponse>;
  signOut(): Promise<void>;
}
