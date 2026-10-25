// $bytes = New-Object byte[] 32
// $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
// $rng.GetBytes($bytes)
// $env:JWT_SECRET = [Convert]::ToBase64String($bytes)
// $rng.Dispose()