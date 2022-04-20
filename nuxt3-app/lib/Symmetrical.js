import CryptoJS from 'crypto-js'

class Symmetrical {
  constructor(roomId) {
    window.localStorage.setItem(`chat:room:${roomId}:secret`, Symmetrical.randomString(50))
  }

  static randomString(len) {
    const charSet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

    let randomString = '';
    for (let i = 0; i < len; i++) {
      let randomPoz = Math.floor(Math.random() * charSet.length);
      randomString += charSet.substring(randomPoz,randomPoz+1);
    }

    return randomString;
  }

  static async encrypt(roomId, plainText) {
    return CryptoJS.AES.encrypt(
      plainText, 
      window.localStorage.getItem(`chat:room:${roomId}:secret`)
    )
      .toString();
  }

  static async decrypt(roomId, cipherText) {
    return CryptoJS.AES.decrypt(
      cipherText, 
      window.localStorage.getItem(`chat:room:${roomId}:secret`)
    )
      .toString(CryptoJS.enc.Utf8);
  }
}

export default Symmetrical
