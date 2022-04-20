class DeffieHellman {
  constructor() {
  }

  static PUBLIC_KEY_JWK = 'PUBLIC_KEY_JWK'
  static PRIVATE_KEY_JWK = 'PRIVATE_KEY_JWK'

  async initialize() {
    const keyPair = await window.crypto.subtle.generateKey(
      { name: "ECDH", namedCurve: "P-256" },
      true,
      ["deriveKey", "deriveBits"]
    );
  
    const publicKeyJwk = await window.crypto.subtle.exportKey("jwk", keyPair.publicKey);
    const privateKeyJwk = await window.crypto.subtle.exportKey("jwk", keyPair.privateKey);

    window.localStorage.setItem(DeffieHellman.PUBLIC_KEY_JWK, JSON.stringify(publicKeyJwk))
    window.localStorage.setItem(DeffieHellman.PRIVATE_KEY_JWK, JSON.stringify(privateKeyJwk))
  }

  static async create() {
    const o = new DeffieHellman()

    await o.initialize()

    return o
  }

  static async getDerivedKey(publicKeyJwk, privateKeyJwk) {
    const publicKey = await window.crypto.subtle.importKey(
      "jwk",
      publicKeyJwk,
      { name: "ECDH", namedCurve: "P-256" },
      true,
      []
    );
  
    const privateKey = await window.crypto.subtle.importKey(
      "jwk",
      privateKeyJwk,
      { name: "ECDH", namedCurve: "P-256" },
      true,
      ["deriveKey", "deriveBits"]
    );

    return window.crypto.subtle.deriveKey(
      { name: "ECDH", public: publicKey },
      privateKey,
      { name: "AES-GCM", length: 256 },
      true,
      ["encrypt", "decrypt"]
    );
  }

  static async encrypt(publicKeyJwk, privateKeyJwk, text) {
    const derivedKey = await this.getDerivedKey(publicKeyJwk, privateKeyJwk)
    const encodedText = new TextEncoder().encode(text);

    const encryptedData = await window.crypto.subtle.encrypt(
      { name: "AES-GCM", iv: new TextEncoder().encode("Initialization Vector") },
      derivedKey,
      encodedText
    );
  
    const uintArray = new Uint8Array(encryptedData);
    const string = String.fromCharCode.apply(null, uintArray);
    const base64Data = btoa(string);
  
    return base64Data;
  }

  static async decrypt(publicKeyJwk, privateKeyJwk, text) {
    try {
      const derivedKey = await this.getDerivedKey(publicKeyJwk, privateKeyJwk)

      const string = atob(text);
      const uintArray = new Uint8Array(
        [...string].map((char) => char.charCodeAt(0))
      );
      const algorithm = {
        name: "AES-GCM",
        iv: new TextEncoder().encode("Initialization Vector"),
      };

      const decryptedData = await window.crypto.subtle.decrypt(
        algorithm,
        derivedKey,
        uintArray
      );
  
      return new TextDecoder().decode(decryptedData);
    } catch (e) {
      return `error decrypting message: ${e}`;
    }
  }
}

export default DeffieHellman