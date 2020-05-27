version = "2"
description = "Faraday ZAP Extension"

zapAddOn {
    addOnName.set("Faraday")
    zapVersion.set("2.9.0")

    manifest {
        author.set("Jorge Luis González Iznaga")
        url.set("https://www.faradaysec.com/")
    }
}

dependencies {
    implementation("org.apache.httpcomponents:httpmime:4.5.2")
}

spotless {
    java {
        // Don't check license nor format/style, 3rd-party add-on.
        clearSteps()
    }
}
