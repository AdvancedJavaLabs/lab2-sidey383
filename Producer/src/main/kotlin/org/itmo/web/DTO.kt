package org.itmo.web

import java.util.UUID

data class FileUploadRequest(
    val file: org.springframework.web.multipart.MultipartFile? = null,
    val placeholder: String = "[NAME]",
    val taskId: String = UUID.randomUUID().toString()
)