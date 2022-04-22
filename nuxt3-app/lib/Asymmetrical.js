import JSEncrypt from 'jsencrypt'

class Asymmetrical {
  constructor() {
    const jsEncrypt = new JSEncrypt(); 

    window.localStorage.setItem(Asymmetrical.RSA_PUBLIC_KEY, jsEncrypt.getPublicKey())
    window.localStorage.setItem(Asymmetrical.RSA_PRIVATE_KEY, jsEncrypt.getPrivateKey())
  }

  static RSA_PUBLIC_KEY = 'rsa:public:key'
  static RSA_PRIVATE_KEY = 'rsa:private:key'

  static encrypt(rsaPublicKey, plainText) {
    const encrypt = new JSEncrypt(); 
    encrypt.setPublicKey(rsaPublicKey);

    return encrypt.encrypt(plainText);
  }

  static decrypt(rsaPrivateKey, encryptedText) {
    const encrypt = new JSEncrypt(); 
    encrypt.setPrivateKey(rsaPrivateKey);

    return encrypt.decrypt(encryptedText)
  }
}

export default Asymmetrical
