class Deffiehellman {
  constructor() {
    this.publicKeyJwk = null;
    this.privateKeyJwk = null;
    this.publicKey = null;
    this.privateKey = null;
    this.derivedKey = null;
  }

  async initialize() {
    const keyPair = await window.crypto.subtle.generateKey(
      {
        name: "ECDH",
        namedCurve: "P-256",
      },
      true,
      ["deriveKey", "deriveBits"]
    );
  
    this.publicKeyJwk = await window.crypto.subtle.exportKey(
      "jwk",
      keyPair.publicKey
    );
  
    this.privateKeyJwk = await window.crypto.subtle.exportKey(
      "jwk",
      keyPair.privateKey
    );

    this.publicKey = await window.crypto.subtle.importKey(
      "jwk",
      this.publicKeyJwk,
      {
        name: "ECDH",
        namedCurve: "P-256",
      },
      true,
      []
    );
  
    this.privateKey = await window.crypto.subtle.importKey(
      "jwk",
      this.privateKeyJwk,
      {
        name: "ECDH",
        namedCurve: "P-256",
      },
      true,
      ["deriveKey", "deriveBits"]
    );

    this.derivedKey = await window.crypto.subtle.deriveKey(
      { name: "ECDH", public: this.publicKey },
      this.privateKey,
      { name: "AES-GCM", length: 256 },
      true,
      ["encrypt", "decrypt"]
    );
  }

  static async create() {
    const o = new Deffiehellman()

    await o.initialize()

    return o
  }

  async encrypt(text) {
    const encodedText = new TextEncoder().encode(text);

    const encryptedData = await window.crypto.subtle.encrypt(
      { name: "AES-GCM", iv: new TextEncoder().encode("Initialization Vector") },
      this.derivedKey,
      encodedText
    );
  
    const uintArray = new Uint8Array(encryptedData);
    const string = String.fromCharCode.apply(null, uintArray);
    const base64Data = btoa(string);
  
    return base64Data;
  }

  async decrypt(text) {
    try {
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
        this.derivedKey,
        uintArray
      );
  
      return new TextDecoder().decode(decryptedData);
    } catch (e) {
      return `error decrypting message: ${e}`;
    }
  }
}

export default Deffiehellman