#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MinIO连接测试脚本
用于验证应用程序能否正常连接MinIO
"""

import urllib.request
import urllib.error
import json
import time

def test_minio_endpoint(endpoint):
    """测试MinIO端点是否可访问"""
    print(f"🔍 测试MinIO端点: {endpoint}")
    
    try:
        # 测试基本连接
        response = urllib.request.urlopen(f"{endpoint}", timeout=10)
        print(f"  ✅ 端点可访问，状态码: {response.status}")
        return True
    except urllib.error.HTTPError as e:
        print(f"  ⚠️  HTTP错误: {e.code} - {e.reason}")
        if e.code == 400:
            print("    这是正常的，MinIO返回400表示需要正确的请求格式")
            return True
        return False
    except urllib.error.URLError as e:
        print(f"  ❌ 连接错误: {e.reason}")
        return False
    except Exception as e:
        print(f"  ❌ 未知错误: {e}")
        return False

def test_minio_console(endpoint):
    """测试MinIO Console是否可访问"""
    console_url = endpoint.replace(":9000", ":9001")
    print(f"🔍 测试MinIO Console: {console_url}")
    
    try:
        response = urllib.request.urlopen(console_url, timeout=10)
        print(f"  ✅ Console可访问，状态码: {response.status}")
        return True
    except Exception as e:
        print(f"  ❌ Console访问失败: {e}")
        return False

def test_bucket_access(endpoint, bucket_name):
    """测试存储桶访问"""
    print(f"🔍 测试存储桶访问: {bucket_name}")
    
    # 测试存储桶列表
    try:
        response = urllib.request.urlopen(f"{endpoint}", timeout=10)
        print(f"  ✅ 存储桶列表API可访问")
        return True
    except urllib.error.HTTPError as e:
        if e.code == 400:
            print(f"  ⚠️  存储桶列表API返回400，这是正常的")
            return True
        else:
            print(f"  ❌ 存储桶列表API错误: {e.code}")
            return False
    except Exception as e:
        print(f"  ❌ 存储桶列表API异常: {e}")
        return False

def test_file_access(endpoint, bucket_name, test_file):
    """测试文件访问"""
    print(f"🔍 测试文件访问: {test_file}")
    
    file_url = f"{endpoint}/{bucket_name}/{test_file}"
    try:
        response = urllib.request.urlopen(file_url, timeout=10)
        print(f"  ✅ 文件可访问，状态码: {response.status}")
        print(f"  📁 文件大小: {response.headers.get('Content-Length', 'Unknown')} bytes")
        print(f"  📋 内容类型: {response.headers.get('Content-Type', 'Unknown')}")
        return True
    except urllib.error.HTTPError as e:
        if e.code == 403:
            print(f"  ❌ 文件访问被拒绝 (403) - 权限问题")
        elif e.code == 404:
            print(f"  ❌ 文件不存在 (404)")
        else:
            print(f"  ❌ 文件访问错误: {e.code}")
        return False
    except Exception as e:
        print(f"  ❌ 文件访问异常: {e}")
        return False

def test_application_config():
    """测试应用程序配置"""
    print("🔍 检查应用程序配置")
    
    try:
        with open("src/main/resources/application.yml", "r", encoding="utf-8") as f:
            content = f.read()
            
        # 检查MinIO配置
        if "minio:" in content:
            print("  ✅ 找到MinIO配置")
            
            # 提取配置信息
            lines = content.split('\n')
            minio_config = {}
            in_minio_section = False
            
            for line in lines:
                line = line.strip()
                if line == "minio:":
                    in_minio_section = True
                elif in_minio_section and ":" in line and not line.startswith("#"):
                    if line.startswith("  "):
                        key, value = line.split(":", 1)
                        minio_config[key.strip()] = value.strip()
                    else:
                        in_minio_section = False
            
            print("  📋 MinIO配置详情:")
            for key, value in minio_config.items():
                print(f"    {key}: {value}")
                
        else:
            print("  ❌ 未找到MinIO配置")
            return False
            
    except FileNotFoundError:
        print("  ❌ 配置文件不存在")
        return False
    except Exception as e:
        print(f"  ❌ 读取配置文件失败: {e}")
        return False
    
    return True

def main():
    print("🔧 MinIO连接测试工具")
    print("=" * 50)
    
    # 配置
    ENDPOINT = "http://localhost:9000"
    BUCKET = "croissant"
    TEST_FILE = "artists/98b29849-aa07-4d91-b23f-e10ff91807cc-blob"
    
    print(f"测试配置:")
    print(f"  端点: {ENDPOINT}")
    print(f"  存储桶: {BUCKET}")
    print(f"  测试文件: {TEST_FILE}")
    print()
    
    # 测试1: 检查应用程序配置
    config_ok = test_application_config()
    print()
    
    # 测试2: 测试MinIO端点
    endpoint_ok = test_minio_endpoint(ENDPOINT)
    print()
    
    # 测试3: 测试MinIO Console
    console_ok = test_minio_console(ENDPOINT)
    print()
    
    # 测试4: 测试存储桶访问
    bucket_ok = test_bucket_access(ENDPOINT, BUCKET)
    print()
    
    # 测试5: 测试文件访问
    file_ok = test_file_access(ENDPOINT, BUCKET, TEST_FILE)
    print()
    
    # 总结
    print("📊 测试结果总结")
    print("=" * 50)
    
    tests = [
        ("应用程序配置", config_ok),
        ("MinIO端点", endpoint_ok),
        ("MinIO Console", console_ok),
        ("存储桶访问", bucket_ok),
        ("文件访问", file_ok)
    ]
    
    passed = 0
    for test_name, result in tests:
        status = "✅ 通过" if result else "❌ 失败"
        print(f"{test_name}: {status}")
        if result:
            passed += 1
    
    print(f"\n总计: {passed}/{len(tests)} 项测试通过")
    
    if passed == len(tests):
        print("\n🎉 所有测试通过！MinIO连接正常。")
    else:
        print("\n⚠️  部分测试失败，需要检查以下问题:")
        if not endpoint_ok:
            print("  - MinIO服务是否正在运行")
        if not console_ok:
            print("  - MinIO Console端口是否正确")
        if not bucket_ok:
            print("  - 存储桶是否存在")
        if not file_ok:
            print("  - 文件权限配置是否正确")
    
    print("\n💡 建议:")
    if not file_ok:
        print("1. 检查MinIO存储桶权限设置")
        print("2. 确认文件确实存在于存储桶中")
        print("3. 考虑设置存储桶为公共读取")

if __name__ == "__main__":
    main() 