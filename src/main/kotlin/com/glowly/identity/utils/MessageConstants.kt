package com.glowly.identity.utils

object MessageConstants {

    object Success {
        const val ACCOUNT_REGISTERED = "Conta registrada com sucesso."
        const val ACCOUNT_REGISTERED_PENDING = "Conta registrada com sucesso. Aguarde a liberação da equipe administrativa."
        const val PASSWORD_CHANGED = "Senha alterada com sucesso."
        const val LOGOUT = "Logout realizado com sucesso."
        const val ACCOUNT_APPROVED = "Conta aprovada com sucesso."
        const val ROLE_UPDATED = "Cargo atualizado com sucesso."
        const val ACCOUNT_BANNED = "Conta bloqueada com sucesso."
        const val ACCOUNT_UNBANNED = "Conta desbloqueada com sucesso."
        const val ACCOUNT_DELETED = "Conta removida com sucesso."
        const val PASSWORD_RECOVERY_SENT = "Se o e-mail estiver cadastrado, você receberá um link de recuperação."
    }

    object Error {
        const val INVALID_LOGIN = "Informe um nome de usuário ou email válido."
        const val INVALID_CPF = "Informe um CPF válido."
        const val INVALID_EMAIL = "Informe um endereço de email válido."
        const val INVALID_PHONE = "Informe um número de telefone válido."
        const val INVALID_PASSWORD_LENGTH = "A senha deve conter entre 6 e 30 caracteres contendo apenas letras, números e símbolos."
        const val INVALID_USERNAME_LENGTH = "O nome de usuário deve conter entre 4 e 20 caracteres."
        const val INVALID_USERNAME_PATTERN = "Username pode conter apenas letras minusculas, números e _"
        const val INVALID_FULLNAME = "Informe o nome completo."
        const val INVALID_STORE_ROLE = "Os campos 'storeId' e 'role' devem ser informados juntos ou ambos omitidos."

        const val INVALID_CNPJ = "Informe um CNPJ válido."
        const val INVALID_STORE_NAME = "O nome da loja deve conter entre 4 e 50 caracteres."
        const val INVALID_STORE_DESCRIPTION = "A descrição da loja deve conter no máximo 255 caracteres."
        const val INVALID_STREET = "O nome da rua deve conter no máximo 50 caracteres."
        const val INVALID_STORE_NUMBER = "O número da loja deve conter no máximo 10 caracteres."
        const val INVALID_STORE_COMPLEMENT = "O complemento da loja deve conter no máximo 50 caracteres."
        const val INVALID_STORE_CITY = "O nome da cidade deve conter no máximo 50 caracteres."
        const val INVALID_STORE_STATE = "O nome do estado deve conter no máximo 50 caracteres."
        const val INVALID_STORE_ZIP_CODE = "O CEP deve conter no máximo 10 caracteres."
        const val INVALID_STORE_WEBSITE = "Informe um URL válido."

        const val STORE_NOT_FOUND = "A loja com o ID informado não existe."
        const val ACCOUNT_NOT_FOUND = "Conta não encontrada."
        const val TOKEN_NOT_FOUND = "Link de recuperação inválido ou não encontrado."
        const val PENDING_ACCOUNTS_NOT_FOUND = "Não há contas pendentes de aprovação."
        const val BANNED_ACCOUNTS_NOT_FOUND = "Não há contas bloqueadas no momento."

        const val ACCOUNT_ALREADY_APPROVED = "Esta conta já foi aprovada anteriormente."
        const val ACCOUNT_ALREADY_BANNED = "Esta conta já está bloqueada."
        const val ACCOUNT_NOT_BANNED = "A conta informada não está bloqueada."
        const val ROLE_ALREADY_ASSIGNED = "A conta já possui o cargo informado."
        const val DUPLICATE_CPF = "Já existe uma conta vinculada a este CPF."
        const val DUPLICATE_USERNAME = "Já existe uma conta com este nome de usuário."
        const val DUPLICATE_EMAIL = "Já existe uma conta vinculada a este email."
        const val DUPLICATE_PHONE = "Já existe uma conta vinculada a este telefone."

        const val INVALID_CREDENTIALS = "Usuário ou senha incorretos."
        const val ACCOUNT_BANNED_PERMANENT = "Conta permanentemente bloqueada. Entre em contato com o suporte."
        const val ACCOUNT_BANNED_TEMPORARY = "Conta temporariamente bloqueada. Aguarde alguns minutos e tente novamente."
        const val ACCOUNT_LOCKED_ATTEMPTS = "Conta bloqueada por excesso de tentativas. Aguarde %d minutos."
        const val ACCOUNT_PENDING = "Conta pendente de aprovação. Aguarde a liberação pela equipe administrativa."

        const val TOKEN_EXPIRED = "Link de recuperação expirado. Solicite uma nova redefinição de senha."

        const val SELF_MANAGEMENT = "Não é possível gerenciar a própria conta enquanto autenticado."
        const val HIERARCHY_VIOLATION = "Você não possui permissão para gerenciar um usuário com cargo igual ou superior ao seu."
    }
}
