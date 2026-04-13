# This file is regenerated on each release by scripts/release-sdk.sh
# The values below are placeholders — replaced when a release is published.
Pod::Spec.new do |spec|
  spec.name         = "VoxaVis"
  spec.version      = "0.0.0"
  spec.summary      = "Cross-platform Compose visualization library for vocal/pitch apps"
  spec.description  = <<-DESC
    Cross-platform Compose visualization library for vocal/pitch apps
  DESC

  spec.homepage     = "https://github.com/musicmuni/voxavis"
  spec.license      = { :type => "Commercial License" }
  spec.author       = { "MusicMuni" => "support@musicmuni.com" }

  spec.ios.deployment_target = "15.0"
  spec.swift_versions = ["5.9"]

  spec.source = {
    :http => "https://github.com/musicmuni/voxavis/releases/download/voxavis-v0.0.0/voxavis.xcframework.zip"
  }

  spec.vendored_frameworks = "VoxaVis.xcframework"
end
