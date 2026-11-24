package com.john.project.common.EncryptDecryptUtil;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.john.project.model.EncryptDecryptModel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.crypto.generators.MLKEMKeyPairGenerator;
import org.bouncycastle.crypto.kems.MLKEMExtractor;
import org.bouncycastle.crypto.kems.MLKEMGenerator;
import org.bouncycastle.crypto.params.MLKEMKeyGenerationParameters;
import org.bouncycastle.crypto.params.MLKEMParameters;
import org.bouncycastle.crypto.params.MLKEMPrivateKeyParameters;
import org.bouncycastle.crypto.params.MLKEMPublicKeyParameters;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.john.project.constant.EncryptDecryptConstant.SECRET_KEY_EXPIRED_DURATION;
import static com.john.project.constant.EncryptDecryptConstant.SECRET_KEY_WAIT_ACTIVATION_DURATION;

@Component
public class EncryptDecryptUtil {

    @Getter
    @Setter
    private List<EncryptDecryptModel> encryptDecryptModelList = new ArrayList<>();

    private final AsyncLoadingCache<Long, String> caffeineLoadCacheForPublicKeyOfMLKEM = Caffeine.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .refreshAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((t) -> {
                var publicKeyOfMLKEM = this.getListOfSecretKey()
                        .select(s -> s.getPublicKeyOfMLKEM())
                        .limit(1)
                        .getOnlyValue();
                return publicKeyOfMLKEM;
            });

    private final AsyncLoadingCache<Long, List<String>> caffeineLoadCacheForPrivateKeyOfMLKEM = Caffeine.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .refreshAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((t) -> {
                var listOfPrivateKeyOfMLKEM = this.getListOfSecretKey()
                        .map(s -> s.getPrivateKeyOfMLKEM())
                        .toList();
                return listOfPrivateKeyOfMLKEM;
            });

    private final AsyncLoadingCache<Long, String> caffeineLoadCacheForSecretKeyOfAESForEncryption = Caffeine.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .refreshAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((t) -> {
                var secretKeyOfAES = this.getListOfSecretKey()
                        .select(s -> s.getSecretKeyOfAES())
                        .limit(1)
                        .getOnlyValue();
                return secretKeyOfAES;
            });

    private final AsyncLoadingCache<Long, List<String>> caffeineLoadCacheForSecretKeyOfAESForDecryption = Caffeine.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .refreshAfterWrite(30, TimeUnit.SECONDS)
            .buildAsync((t) -> {
                var listOfSecretKeyOfAES = this.getListOfSecretKey()
                        .map(s -> s.getSecretKeyOfAES())
                        .toList();
                return listOfSecretKeyOfAES;
            });

    public String encryptByAES(String text) {
        var secretKeyOfAES = this.getKeyOfSecretKeyOfAESForEncryption();
        return this.encryptByAES(text, secretKeyOfAES);
    }

    public String decryptByAES(String text) {
        var listOfSecretKeyOfAES = this.getKeyOfSecretKeyOfAESForDecryption();
        for (var i = listOfSecretKeyOfAES.size() - 1; i >= 0; i--) {
            var secretKeyOfAES = listOfSecretKeyOfAES.get(i);
            if (i == 0) {
                return this.decryptByAES(text, secretKeyOfAES);
            } else {
                try {
                    return this.decryptByAES(text, secretKeyOfAES);
                } catch (Throwable e) {
                    // do nothing
                }
            }
        }
        throw new NotImplementedException("The key cannot be empty");
    }

    public String encryptByPublicKeyOfMLKEM(String text) {
        var publicKeyOfMLKEM = this.getKeyOfPublicKeyOfMLKEM();
        return this.encryptByPublicKeyOfMLKEM(text, publicKeyOfMLKEM);
    }

    public String decryptByByPrivateKeyOfMLKEM(String text) {
        var listOfPrivateKeyOfMLKEM = this.getKeyOfPrivateKeyOfMLKEM();
        for (var i = listOfPrivateKeyOfMLKEM.size() - 1; i >= 0; i--) {
            var privateKeyOfMLKEM = listOfPrivateKeyOfMLKEM.get(i);
            if (i == 0) {
                return this.decryptByByPrivateKeyOfMLKEM(text, privateKeyOfMLKEM);
            } else {
                try {
                    return this.decryptByByPrivateKeyOfMLKEM(text, privateKeyOfMLKEM);
                } catch (Throwable e) {
                    // do nothing
                }
            }
        }
        throw new NotImplementedException("The key cannot be empty");
    }

    @SneakyThrows
    public String encryptByAES(String text, String secretKeyOfAES) {
        var salt = RandomUtil.randomBytes(16);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                HexUtil.decodeHex(secretKeyOfAES), "AES"), new GCMParameterSpec(16 * 8, salt));
        return HexUtil.encodeHexStr(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @SneakyThrows
    public String decryptByAES(String text, String secretKeyOfAES) {
        var textByteList = HexUtil.decodeHex(text);
        var salt = ArrayUtils.subarray(textByteList, 0, 16);
        var encryptedTextByteList = ArrayUtils.subarray(textByteList, 16, textByteList.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(
                HexUtil.decodeHex(secretKeyOfAES), "AES"), new GCMParameterSpec(16 * 8, salt));
        return new String(cipher.doFinal(encryptedTextByteList), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public String encryptWithFixedSaltByAES(String text) {
        var secretKeyOfAES = new SecretKeySpec(HexUtil.decodeHex(this.getKeyOfSecretKeyOfAESForEncryption()), "AES");
        var salt = DigestUtils.md5(text);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeyOfAES, new GCMParameterSpec(16 * 8, salt));
        return HexUtil.encodeHexStr(ArrayUtils.addAll(salt, cipher.doFinal(text.getBytes(StandardCharsets.UTF_8))));
    }

    @SneakyThrows
    public String encryptByPublicKeyOfMLKEM(String text, String publicKeyOfMLKEM) {
        var generatorOfMLKEM = new MLKEMGenerator(RandomUtil.getSecureRandom());
        var keyOfPublicKeyOfMLKEM = new MLKEMPublicKeyParameters(MLKEMParameters.ml_kem_1024, HexUtil.decodeHex(publicKeyOfMLKEM));
        var encapsulatedOfMLKEM = generatorOfMLKEM.generateEncapsulated(keyOfPublicKeyOfMLKEM);
        var sharedSecretKeyOfAES = HexUtil.encodeHexStr(encapsulatedOfMLKEM.getSecret());
        var encapsulateCiphertextByteList = encapsulatedOfMLKEM.getEncapsulation();
        var resultOfEncryptByAES = this.encryptByAES(text, sharedSecretKeyOfAES);
        return HexUtil.encodeHexStr(ArrayUtils.addAll(encapsulateCiphertextByteList, HexUtil.decodeHex(resultOfEncryptByAES)));
    }

    @SneakyThrows
    public String decryptByByPrivateKeyOfMLKEM(String text, String privateKeyOfMLKEM) {
        var textByteList = HexUtil.decodeHex(text);
        var encapsulateCipherByteList = ArrayUtils.subarray(textByteList, 0, 1568);
        var encryptedText = HexUtil.encodeHexStr(ArrayUtils.subarray(textByteList, 1568, textByteList.length));
        var keyOfPrivateKeyOfMLKEM = new MLKEMPrivateKeyParameters(MLKEMParameters.ml_kem_1024, HexUtil.decodeHex(privateKeyOfMLKEM));
        var extractorOfMLKEM = new MLKEMExtractor(keyOfPrivateKeyOfMLKEM);
        var sharedSecretKeyOfAES = HexUtil.encodeHexStr(extractorOfMLKEM.extractSecret(encapsulateCipherByteList));
        return this.decryptByAES(encryptedText, sharedSecretKeyOfAES);
    }

    @SneakyThrows
    public String generateSecretKeyOfAES() {
        var keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return HexUtil.encodeHexStr(keyGenerator.generateKey().getEncoded());
    }

    @SneakyThrows
    public String generateSecretKeyOfAES(String password) {
        var salt = DigestUtils.md5(password);
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        var spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        var secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return HexUtil.encodeHexStr(secret.getEncoded());
    }

    @SneakyThrows
    public EncryptDecryptModel generateKeyPairOfMLKEM() {
        var keyPairGeneratorOfMLKEM = new MLKEMKeyPairGenerator();
        keyPairGeneratorOfMLKEM.init(new MLKEMKeyGenerationParameters(RandomUtil.getSecureRandom(), MLKEMParameters.ml_kem_1024));
        var keyPairOfMLKEM = keyPairGeneratorOfMLKEM.generateKeyPair();
        var publicKeyOfMLKEM = (MLKEMPublicKeyParameters) keyPairOfMLKEM.getPublic();
        var privateKeyOfMLKEM = (MLKEMPrivateKeyParameters) keyPairOfMLKEM.getPrivate();
        return new EncryptDecryptModel()
                .setPublicKeyOfMLKEM(HexUtil.encodeHexStr(publicKeyOfMLKEM.getEncoded()))
                .setPrivateKeyOfMLKEM(HexUtil.encodeHexStr(privateKeyOfMLKEM.getEncoded()));
    }

    @SneakyThrows
    public String getKeyOfPublicKeyOfMLKEM() {
        return caffeineLoadCacheForPublicKeyOfMLKEM.get(NumberUtils.LONG_ZERO).get();
    }

    @SneakyThrows
    private List<String> getKeyOfPrivateKeyOfMLKEM() {
        return caffeineLoadCacheForPrivateKeyOfMLKEM.get(NumberUtils.LONG_ZERO).get();
    }

    @SneakyThrows
    private String getKeyOfSecretKeyOfAESForEncryption() {
        return caffeineLoadCacheForSecretKeyOfAESForEncryption.get(NumberUtils.LONG_ZERO).get();
    }

    @SneakyThrows
    private List<String> getKeyOfSecretKeyOfAESForDecryption() {
        return caffeineLoadCacheForSecretKeyOfAESForDecryption.get(NumberUtils.LONG_ZERO).get();
    }

    private JinqStream<EncryptDecryptModel> getListOfSecretKey() {
        while (this.encryptDecryptModelList.isEmpty()) {
            ThreadUtils.sleepQuietly(Duration.ofMillis(100));
        }
        var waitActivationDate = DateUtils.addSeconds(new Date(), -(int) SECRET_KEY_WAIT_ACTIVATION_DURATION.toSeconds());
        var expiringSoonDate = DateUtils.addSeconds(new Date(), -(int) SECRET_KEY_EXPIRED_DURATION.plusSeconds(-SECRET_KEY_WAIT_ACTIVATION_DURATION.toSeconds()).toSeconds());
        return JinqStream.from(encryptDecryptModelList)
                .sortedBy(s -> s.getName())
                .sortedDescendingBy(s -> s.getCreateDate())
                .sortedBy(s -> s.getCreateDate().before(waitActivationDate) ?
                        waitActivationDate :
                        s.getCreateDate())
                .sortedDescendingBy(s -> s.getCreateDate().before(expiringSoonDate) ? s.getCreateDate() : expiringSoonDate);
    }

}

