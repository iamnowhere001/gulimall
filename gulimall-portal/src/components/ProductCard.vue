<template>
  <router-link :to="`/product/${product.skuId}`" class="product-card">
    <img
      :src="product.skuDefaultImg || product.skuImg"
      :alt="product.skuTitle"
      class="product-card__img"
    />
    <p class="product-card__title">{{ product.skuTitle }}</p>
    <p class="product-card__price">
      ￥{{ product.price != null ? product.price : product.skuPrice }}
    </p>
  </router-link>
</template>

<script setup>
defineProps({
  product: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.product-card {
  display: block;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 12px;
  text-decoration: none;
  color: #333;
  transition: box-shadow 0.2s;
  background: #fff;
}
.product-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
}
.product-card__img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}
.product-card__title {
  font-size: 13px;
  margin: 8px 0;
  height: 36px;
  overflow: hidden;
}
.product-card__price {
  color: #e1251b;
  font-weight: 700;
  font-size: 16px;
}
</style>
