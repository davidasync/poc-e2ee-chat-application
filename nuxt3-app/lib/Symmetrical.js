class Symmetrical {
  constructor() {
    this.algoEncrypt = null
    this.secretKey = null
  }

  async initialize() {
    const iv = await window.crypto.getRandomValues(new Uint8Array(12));
    const keyUsages = ["encrypt", "decrypt"];

    const algoKeyGen = {
      name: "AES-GCM",
      length: 256,
    };

    this.algoEncrypt = {
      name: "AES-GCM",
      iv: iv,
      tagLength: 128,
    };

    this.secretKey = await window.crypto.subtle.generateKey(algoKeyGen, false, keyUsages)
  }

  // workaround to achieve async constructor
  static async create() {
    const o = new Symmetrical()

    await o.initialize()

    return o
  }

  /*The function strToArrayBuffer converts string to fixed-length raw binary data buffer because 
  encrypt method must return a Promise that fulfills with an ArrayBuffer containing the "ciphertext"*/
  static strToArrayBuffer = (str) => {
    var buf = new ArrayBuffer(str.length * 2);
    var bufView = new Uint16Array(buf);

    for (var i = 0, strLen = str.length; i < strLen; i++) {
      bufView[i] = str.charCodeAt(i);
    }

    return buf;
  }

  //The function arrayBufferToString converts fixed-length raw binary data buffer to 16-bit unsigned String as our plaintext
  static arrayBufferToString = (buf) => {
    return String.fromCharCode.apply(null, new Uint16Array(buf));
  }

  async encrypt(plainText) {
    const cipherText = await window.crypto.subtle.encrypt(
      this.algoEncrypt,
      this.secretKey,
      Symmetrical.strToArrayBuffer(plainText)
    );

    return Symmetrical.arrayBufferToString(cipherText)
  }

  async decrypt(cipherText) {
    const plainText = await window.crypto.subtle.decrypt(
      this.algoEncrypt,
      this.secretKey,
      Symmetrical.strToArrayBuffer(cipherText),
    );

    return Symmetrical.arrayBufferToString(plainText)
  }
}

export default Symmetrical
