package org.itmo.web

import org.itmo.TaskProducer
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile


@Controller
class FileUploadController(
    private val taskProducer: TaskProducer
) {

    @GetMapping(value = ["/", "/upload"])
    fun uploadForm(model: Model): String {
        model.addAttribute("uploadRequest", FileUploadRequest())
        return "upload"
    }

    @PostMapping("/upload")
    fun handleFileUpload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("placeholder", defaultValue = "[NAME]") placeholder: String,
        @RequestParam("taskId", required = false) taskId: String?,
        model: Model
    ): String {
        return try {
            if (file.isEmpty) {
                model.addAttribute("error", "Please select a file to upload")
                return "upload"
            }

            val finalTaskId = taskProducer.startTask(file.inputStream, placeholder, taskId)

            model.addAttribute("message", "File uploaded and processed successfully! Task ID: $finalTaskId")
            model.addAttribute("uploadRequest", FileUploadRequest(placeholder = placeholder))
            "upload"
        } catch (e: Exception) {
            model.addAttribute("error", "Error processing file: ${e.message}")
            model.addAttribute("uploadRequest", FileUploadRequest(placeholder = placeholder))
            "upload"
        }
    }

}