package com.woodyhi.adb;

import com.cgutman.adblib.AdbCrypto;
import com.woodyhi.adb.Base64Impl;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by June on 2018/6/21.
 */
public class AdbCryptoUtil {


    // This function loads a keypair from the specified files if one exists, and if not,
    // it creates a new keypair and saves it in the specified files
    public static AdbCrypto setupCrypto(String pubKeyFile, String privKeyFile)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
    {
        File pub = new File(pubKeyFile);
        File priv = new File(privKeyFile);
        AdbCrypto c = null;

        // Try to load a key pair from the files
        if (pub.exists() && priv.exists())
        {
            try {
                c = AdbCrypto.loadAdbKeyPair(new Base64Impl(), priv, pub);
            } catch (IOException e) {
                // Failed to read from file
                c = null;
            } catch (InvalidKeySpecException e) {
                // Key spec was invalid
                c = null;
            } catch (NoSuchAlgorithmException e) {
                // RSA algorithm was unsupported with the crypo packages available
                c = null;
            }
        }

        if (c == null)
        {
            // We couldn't load a key, so let's generate a new one
            c = AdbCrypto.generateAdbKeyPair(new Base64Impl());

            // Save it
            c.saveAdbKeyPair(priv, pub);
            System.out.println("Generated new keypair");
        }
        else
        {
            System.out.println("Loaded existing keypair");
        }

        return c;
    }

}
