package publishing

plugins {
    id("com.vanniktech.maven.publish")
}

project.extensions.create("elmsliePublishing", PublishingExtension::class.java, project)
