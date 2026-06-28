#!/usr/bin/env ruby
# frozen_string_literal: true

# Adds the `iosAppUITests` UI Testing Bundle target to iosApp.xcodeproj so that
# `fastlane snapshot` / the `screenshots` lane can drive ScreenshotUITests.swift.
#
# Idempotent: if the target already exists it does nothing. Uses the `xcodeproj`
# gem (bundled with fastlane), so no manual Xcode steps are required.
#
#   cd iosApp && bundle exec ruby scripts/setup_screenshots_target.rb
#
# This replaces the previous manual "File ▸ New ▸ Target" instructions.

require "xcodeproj"

APP_TARGET_NAME   = "iosApp"
UI_TEST_NAME      = "iosAppUITests"
UI_TEST_BUNDLE_ID = "com.apptolast.familyfilmapp.uitests"
DEVELOPMENT_TEAM  = "3NXH5U7C5A"
SWIFT_VERSION     = "5.0"

project_path = File.expand_path(File.join(__dir__, "..", "iosApp.xcodeproj"))
project = Xcodeproj::Project.open(project_path)

app_target = project.targets.find { |t| t.name == APP_TARGET_NAME }
raise "App target '#{APP_TARGET_NAME}' not found in #{project_path}" if app_target.nil?

if project.targets.any? { |t| t.name == UI_TEST_NAME }
  puts "✓ Target '#{UI_TEST_NAME}' already exists — nothing to do."
  exit 0
end

deployment_target =
  app_target.build_configurations.first.build_settings["IPHONEOS_DEPLOYMENT_TARGET"] || "16.0"

# 1) Create the UI testing bundle target.
ui_test_target = project.new_target(
  :ui_test_bundle,
  UI_TEST_NAME,
  :ios,
  deployment_target,
  nil,
  :swift,
)

# 2) Build settings: tie it to the app under test and let Xcode synthesize Info.plist.
ui_test_target.build_configurations.each do |config|
  bs = config.build_settings
  bs["PRODUCT_BUNDLE_IDENTIFIER"]      = UI_TEST_BUNDLE_ID
  bs["TEST_TARGET_NAME"]               = APP_TARGET_NAME
  bs["GENERATE_INFOPLIST_FILE"]        = "YES"
  bs["SWIFT_VERSION"]                  = SWIFT_VERSION
  bs["CODE_SIGN_STYLE"]                = "Automatic"
  bs["DEVELOPMENT_TEAM"]               = DEVELOPMENT_TEAM
  bs["IPHONEOS_DEPLOYMENT_TARGET"]     = deployment_target
  bs["TARGETED_DEVICE_FAMILY"]         = "1,2"
  bs["PRODUCT_NAME"]                   = "$(TARGET_NAME)"
  bs["SWIFT_EMIT_LOC_STRINGS"]         = "NO"
  bs["ALWAYS_EMBED_SWIFT_STANDARD_LIBRARIES"] = "NO"
end

# 3) Attach the source files that already live on disk under iosAppUITests/.
group = project.main_group.find_subpath(UI_TEST_NAME, true)
group.set_source_tree("SOURCE_ROOT")
group.set_path(UI_TEST_NAME)

%w[SnapshotHelper.swift ScreenshotUITests.swift].each do |file_name|
  disk_path = File.join(__dir__, "..", UI_TEST_NAME, file_name)
  raise "Missing source file: #{disk_path}" unless File.exist?(disk_path)

  file_ref = group.files.find { |f| f.path == file_name } || group.new_reference(file_name)
  ui_test_target.add_file_references([file_ref])
end

# 4) Build the app before the tests run.
ui_test_target.add_dependency(app_target)

project.save

# 5) Register the test bundle in the shared `iosApp` scheme's Test action.
scheme_path = File.join(
  project_path, "xcshareddata", "xcschemes", "#{APP_TARGET_NAME}.xcscheme",
)
if File.exist?(scheme_path)
  scheme = Xcodeproj::XCScheme.new(scheme_path)
  already = scheme.test_action.testables.any? do |t|
    t.buildable_references.any? { |r| r.target_name == UI_TEST_NAME }
  end
  unless already
    scheme.test_action.add_testable(
      Xcodeproj::XCScheme::TestAction::TestableReference.new(ui_test_target),
    )
    scheme.save_as(project_path, APP_TARGET_NAME, true)
  end
  puts "✓ Registered '#{UI_TEST_NAME}' in the '#{APP_TARGET_NAME}' scheme Test action."
else
  warn "! Shared scheme not found at #{scheme_path}; add the test bundle to the scheme's Test action manually."
end

puts "✓ Added UI testing target '#{UI_TEST_NAME}' to #{File.basename(project_path)}."
puts "  Run screenshots with:  cd iosApp && bundle exec fastlane screenshots"
