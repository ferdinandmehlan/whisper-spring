# whisper-spring-test-common

Shared test utilities and base classes for testing Whisper Spring modules.

## Overview

This module provides common testing infrastructure used across all Whisper Spring modules, including:

- Base test classes with common setup
- HTTP response validation utilities
- File-based assertion helpers
- Test data management

## Usage

Add as a test dependency in your module's build.gradle:

```gradle
testImplementation project(':whisper-spring-test-common')
```

## Key Classes

- `BaseTest` - Base class for integration tests with common configuration
- `ResponseEntityValidationTraits` - Utilities for validating HTTP responses
- File assertion methods for comparing test outputs with expected files

## Testing Approach

The module supports file-based testing where test outputs are compared against expected result files.  
This ensures consistent and maintainable tests for transcription accuracy.
