package com.ottogo.weekly.authenticator

import android.accounts.*
import android.accounts.AccountManager.KEY_BOOLEAN_RESULT
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.ottogo.weekly.MainActivity


class Authenticator(context: Context) : AbstractAccountAuthenticator(context) {
    private val TAG = "UdinicAuthenticator"
    private val mContext: Context

    val ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE"
    val ARG_AUTH_TYPE = "AUTH_TYPE"
    val ARG_ACCOUNT_NAME = "ACCOUNT_NAME"
    val ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT"

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle
    ): Bundle {
        val intent = Intent(mContext, MainActivity::class.java)
        intent.putExtra(ARG_ACCOUNT_TYPE, accountType)
        intent.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle {
        Log.d("udinic", "$TAG> getAuthToken")



        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        val am = AccountManager.get(mContext)
        var authToken = am.peekAuthToken(account, authTokenType)
        Log.d("udinic", "$TAG> peekAuthToken returned - $authToken")

//        // Lets give another try to authenticate the user
//        if (TextUtils.isEmpty(authToken)) {
//            val password = am.getPassword(account)
//            if (password != null) {
//                try {
//                    Log.d("udinic", "$TAG> re-authenticating with the existing password")
//                    authToken =
//                        sServerAuthenticate.userSignIn(account.name, password, authTokenType)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            val result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            return result
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        val intent = Intent(mContext, MainActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(ARG_ACCOUNT_TYPE, account.type)
        intent.putExtra(ARG_AUTH_TYPE, authTokenType)
        intent.putExtra(ARG_ACCOUNT_NAME, account.name)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    override fun getAuthTokenLabel(p0: String?): String {
        TODO("Not yet implemented")
        return ""
    }


    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<String>
    ): Bundle {
        val result = Bundle()
        result.putBoolean(KEY_BOOLEAN_RESULT, false)
        return result
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle
    ): Bundle? {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle? {
        return null
    }

    init {

        // I hate you! Google - set mContext as protected!
        mContext = context
    }
}