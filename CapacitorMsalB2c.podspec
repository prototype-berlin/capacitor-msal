
  Pod::Spec.new do |s|
    s.name = 'CapacitorMsalB2c'
    s.version = '0.1.4'
    s.summary = 'msal plugin for capacitor'
    s.license = 'MIT'
    s.homepage = 'none'
    s.author = 'prototype.berlin GmbH'
    s.source = { :git => 'none', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
    s.dependency 'MSAL'
  end