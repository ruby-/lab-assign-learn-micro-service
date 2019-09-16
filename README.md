![](docs/arch.png)
# User service
- Login
- Registry
- User Info
- State-less (no session), use Redis as unified cache storage
- Single-point access
- Has its own DB
- Registry procedure requires Message service

# Message service
- Send email/ MSG
- Use another language (Python) for learning purpose*

# API gateway
- Accessed by client directly, expose all other micro services

# User edge service
- Accessed by API gateway
- Expose REST API to getway
- Access User service via Thrift RPC

# shopping cart edge service
- Accessed by API gateway
- Expose REST API to getway
- Access shopping cart via Dubbo RPC
